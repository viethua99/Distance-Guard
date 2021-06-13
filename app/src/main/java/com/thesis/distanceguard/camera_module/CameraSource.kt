/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thesis.distanceguard.camera_module

import com.thesis.distanceguard.util.BarcodePreferenceUtils
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.Parameters
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.google.android.gms.common.images.Size
import com.thesis.distanceguard.util.BarcodeUtils
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.IOException
import java.nio.ByteBuffer
import java.util.IdentityHashMap
import kotlin.math.abs
import kotlin.math.ceil


@Suppress("DEPRECATION")
class CameraSource(private val graphicOverlay: GraphicOverlay) {

    private var camera: Camera? = null
    @FirebaseVisionImageMetadata.Rotation
    private var rotation: Int = 0

    internal var previewSize: Size? = null
        private set


    private var processingThread: Thread? = null
    private val processingRunnable = FrameProcessingRunnable()

    private val processorLock = Object()
    private var frameProcessor: FrameProcessor? = null


    private val bytesToByteBuffer = IdentityHashMap<ByteArray, ByteBuffer>()
    private val context: Context = graphicOverlay.context


    @Synchronized
    @Throws(IOException::class)
    internal fun start(surfaceHolder: SurfaceHolder) {
        if (camera != null) return

        camera = createCamera().apply {
            setPreviewDisplay(surfaceHolder)
            startPreview()
        }

        processingThread = Thread(processingRunnable).apply {
            processingRunnable.setActive(true)
            start()
        }
    }


    @Synchronized
    internal fun stop() {
        processingRunnable.setActive(false)
        processingThread?.let {
            try {

                it.join()
            } catch (e: InterruptedException) {
                Log.e(TAG, "Frame processing thread interrupted on stop.")
            }
            processingThread = null
        }

        camera?.let {
            it.stopPreview()
            it.setPreviewCallbackWithBuffer(null)
            try {
                it.setPreviewDisplay(null)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to clear camera preview: $e")
            }
            it.release()
            camera = null
        }

        bytesToByteBuffer.clear()
    }

    fun release() {
        graphicOverlay.clear()
        synchronized(processorLock) {
            stop()
            frameProcessor?.stop()
        }
    }

    fun setFrameProcessor(processor: FrameProcessor) {
        graphicOverlay.clear()
        synchronized(processorLock) {
            frameProcessor?.stop()
            frameProcessor = processor
        }
    }

    fun updateFlashMode(flashMode: String) {
        val parameters = camera?.parameters
        parameters?.flashMode = flashMode
        camera?.parameters = parameters
    }


    @Throws(IOException::class)
    private fun createCamera(): Camera {
        val camera = Camera.open() ?: throw IOException("There is no back-facing camera.")
        val parameters = camera.parameters
        setPreviewAndPictureSize(camera, parameters)
        setRotation(camera, parameters)

        val previewFpsRange = selectPreviewFpsRange(camera)
                ?: throw IOException("Could not find suitable preview frames per second range.")
        parameters.setPreviewFpsRange(
                previewFpsRange[Parameters.PREVIEW_FPS_MIN_INDEX],
                previewFpsRange[Parameters.PREVIEW_FPS_MAX_INDEX]
        )

        parameters.previewFormat = IMAGE_FORMAT

        if (parameters.supportedFocusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.focusMode = Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
        } else {
            Log.i(TAG, "Camera auto focus is not supported on this device.")
        }

        camera.parameters = parameters

        camera.setPreviewCallbackWithBuffer(processingRunnable::setNextFrame)


        previewSize?.let {
            camera.addCallbackBuffer(createPreviewBuffer(it))
            camera.addCallbackBuffer(createPreviewBuffer(it))
            camera.addCallbackBuffer(createPreviewBuffer(it))
            camera.addCallbackBuffer(createPreviewBuffer(it))
        }

        return camera
    }

    @Throws(IOException::class)
    private fun setPreviewAndPictureSize(camera: Camera, parameters: Parameters) {

        val sizePair: CameraSizePair = BarcodePreferenceUtils.getUserSpecifiedPreviewSize(context) ?: run {

            val displayAspectRatioInLandscape: Float =
                    if (BarcodeUtils.isPortraitMode(graphicOverlay.context)) {
                        graphicOverlay.height.toFloat() / graphicOverlay.width
                    } else {
                        graphicOverlay.width.toFloat() / graphicOverlay.height
                    }
            selectSizePair(camera, displayAspectRatioInLandscape)
        } ?: throw IOException("Could not find suitable preview size.")

        previewSize = sizePair.preview.also {
            Log.v(TAG, "Camera preview size: $it")
            parameters.setPreviewSize(it.width, it.height)
        }

        sizePair.picture?.let { pictureSize ->
            Log.v(TAG, "Camera picture size: $pictureSize")
            parameters.setPictureSize(pictureSize.width, pictureSize.height)
        }
    }


    private fun setRotation(camera: Camera, parameters: Parameters) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val degrees = when (val deviceRotation = windowManager.defaultDisplay.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> {
                Log.e(TAG, "Bad device rotation value: $deviceRotation")
                0
            }
        }

        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(CAMERA_FACING_BACK, cameraInfo)
        val angle = (cameraInfo.orientation - degrees + 360) % 360
        // This corresponds to the rotation constants in FirebaseVisionImageMetadata.
        this.rotation = angle / 90
        camera.setDisplayOrientation(angle)
        parameters.setRotation(angle)
    }


    private fun createPreviewBuffer(previewSize: Size): ByteArray {
        val bitsPerPixel = ImageFormat.getBitsPerPixel(IMAGE_FORMAT)
        val sizeInBits = previewSize.height.toLong() * previewSize.width.toLong() * bitsPerPixel.toLong()
        val bufferSize = ceil(sizeInBits / 8.0).toInt() + 1


        val byteArray = ByteArray(bufferSize)
        val byteBuffer = ByteBuffer.wrap(byteArray)
        check(!(!byteBuffer.hasArray() || !byteBuffer.array()!!.contentEquals(byteArray))) {
            // This should never happen. If it does, then we wouldn't be passing the preview content to
            // the underlying detector later.
            "Failed to create valid buffer for camera source."
        }

        bytesToByteBuffer[byteArray] = byteBuffer
        return byteArray
    }


    private inner class FrameProcessingRunnable internal constructor() : Runnable {

        private val lock = Object()
        private var active = true

        private var pendingFrameData: ByteBuffer? = null

        internal fun setActive(active: Boolean) {
            synchronized(lock) {
                this.active = active
                lock.notifyAll()
            }
        }


        internal fun setNextFrame(data: ByteArray, camera: Camera) {
            synchronized(lock) {
                pendingFrameData?.let {
                    camera.addCallbackBuffer(it.array())
                    pendingFrameData = null
                }

                if (!bytesToByteBuffer.containsKey(data)) {
                    Log.d(
                            TAG,
                            "Skipping frame. Could not find ByteBuffer associated with the image data from the camera."
                    )
                    return
                }

                pendingFrameData = bytesToByteBuffer[data]

                lock.notifyAll()
            }
        }


        override fun run() {
            var data: ByteBuffer?

            while (true) {
                synchronized(lock) {
                    while (active && pendingFrameData == null) {
                        try {
                            lock.wait()
                        } catch (e: InterruptedException) {
                            Log.e(TAG, "Frame processing loop terminated.", e)
                            return
                        }
                    }

                    if (!active) {

                        return
                    }


                    data = pendingFrameData
                    pendingFrameData = null
                }

                try {
                    synchronized(processorLock) {
                        val frameMetadata = FrameMetadata(previewSize!!.width, previewSize!!.height, rotation)
                        data?.let {
                            frameProcessor?.process(it, frameMetadata, graphicOverlay)
                        }
                    }
                } catch (t: Exception) {
                    Log.e(TAG, "Exception thrown from receiver.", t)
                } finally {
                    data?.let {
                        camera?.addCallbackBuffer(it.array())
                    }
                }
            }
        }
    }

    companion object {

        const val CAMERA_FACING_BACK = CameraInfo.CAMERA_FACING_BACK

        private const val TAG = "CameraSource"

        private const val IMAGE_FORMAT = ImageFormat.NV21
        private const val MIN_CAMERA_PREVIEW_WIDTH = 400
        private const val MAX_CAMERA_PREVIEW_WIDTH = 1300
        private const val DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH = 640
        private const val DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT = 360
        private const val REQUESTED_CAMERA_FPS = 30.0f


        private fun selectSizePair(camera: Camera, displayAspectRatioInLandscape: Float): CameraSizePair? {
            val validPreviewSizes = BarcodeUtils.generateValidPreviewSizeList(camera)

            var selectedPair: CameraSizePair? = null
            var minAspectRatioDiff = Float.MAX_VALUE

            for (sizePair in validPreviewSizes) {
                Log.d(TAG,"validSize= ${sizePair.preview}")
                val previewSize = sizePair.preview
                if (previewSize.width < MIN_CAMERA_PREVIEW_WIDTH || previewSize.width > MAX_CAMERA_PREVIEW_WIDTH) {
                    continue
                }

                val previewAspectRatio: Float = previewSize.width.toFloat() / previewSize.height.toFloat()
                val aspectRatioDiff = abs((displayAspectRatioInLandscape - previewAspectRatio))
                if (abs(aspectRatioDiff - minAspectRatioDiff) < BarcodeUtils.ASPECT_RATIO_TOLERANCE) {
                    if (selectedPair == null || selectedPair.preview.width < (sizePair.preview.width as Int)) {
                        selectedPair = sizePair
                    }
                } else if (aspectRatioDiff < minAspectRatioDiff) {
                    minAspectRatioDiff = aspectRatioDiff
                    selectedPair = sizePair
                }
            }

            if (selectedPair == null) {

                var minDiff = Integer.MAX_VALUE
                for (sizePair in validPreviewSizes) {
                    val size = sizePair.preview
                    val diff =
                            abs((size.width - DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH) as Int) +
                                    abs((size.height - DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT) as Int)
                    if (diff < minDiff) {
                        selectedPair = sizePair
                        minDiff = diff
                    }
                }
            }

            return selectedPair
        }


        private fun selectPreviewFpsRange(camera: Camera): IntArray? {

            val desiredPreviewFpsScaled = (REQUESTED_CAMERA_FPS * 1000f).toInt()


            var selectedFpsRange: IntArray? = null
            var minDiff = Integer.MAX_VALUE
            for (range in camera.parameters.supportedPreviewFpsRange) {
                val deltaMin = desiredPreviewFpsScaled - range[Parameters.PREVIEW_FPS_MIN_INDEX]
                val deltaMax = desiredPreviewFpsScaled - range[Parameters.PREVIEW_FPS_MAX_INDEX]
                val diff = abs(deltaMin) + abs(deltaMax)
                if (diff < minDiff) {
                    selectedFpsRange = range
                    minDiff = diff
                }
            }
            return selectedFpsRange
        }
    }
}
