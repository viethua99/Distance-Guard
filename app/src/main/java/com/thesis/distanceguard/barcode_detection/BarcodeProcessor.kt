package com.thesis.distanceguard.barcode_detection

import android.animation.ValueAnimator
import android.util.Log
import androidx.annotation.MainThread
import com.google.android.gms.tasks.Task
import com.thesis.distanceguard.camera_module.CameraReticleAnimator
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.thesis.distanceguard.camera_module.GraphicOverlay
import com.thesis.distanceguard.presentation.camera.CameraViewModel
import com.thesis.distanceguard.presentation.camera.CameraViewModel.WorkflowState
import com.thesis.distanceguard.camera_module.FrameProcessorBase
import com.thesis.distanceguard.util.BarcodePreferenceUtils
import java.io.IOException

class BarcodeProcessor(graphicOverlay: GraphicOverlay, private val cameraViewModel: CameraViewModel) :
    FrameProcessorBase<List<FirebaseVisionBarcode>>() {

    private val detector = FirebaseVision.getInstance().visionBarcodeDetector
    private val cameraReticleAnimator: CameraReticleAnimator = CameraReticleAnimator(graphicOverlay)

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionBarcode>> =
        detector.detectInImage(image)

    @MainThread
    override fun onSuccess(
        image: FirebaseVisionImage,
        results: List<FirebaseVisionBarcode>,
        graphicOverlay: GraphicOverlay
    ) {

        if (!cameraViewModel.isCameraLive) return

        Log.d(TAG, "Barcode result size: ${results.size}")


        val barcodeInCenter = results.firstOrNull { barcode ->
            val boundingBox = barcode.boundingBox ?: return@firstOrNull false
            val box = graphicOverlay.translateRect(boundingBox)
            box.contains(graphicOverlay.width / 2f, graphicOverlay.height / 2f)
        }

        graphicOverlay.clear()
        if (barcodeInCenter == null) {
            cameraReticleAnimator.start()
            graphicOverlay.add(BarcodeReticleGraphic(graphicOverlay, cameraReticleAnimator))
            cameraViewModel.setWorkflowState(WorkflowState.DETECTING)
        } else {
            cameraReticleAnimator.cancel()
            val sizeProgress = BarcodePreferenceUtils.getProgressToMeetBarcodeSizeRequirement(graphicOverlay, barcodeInCenter)
            if (sizeProgress < 1) {
                graphicOverlay.add(BarcodeConfirmingGraphic(graphicOverlay, barcodeInCenter))
                cameraViewModel.setWorkflowState(WorkflowState.CONFIRMING)
            } else {
                if (BarcodePreferenceUtils.shouldDelayLoadingBarcodeResult(graphicOverlay.context)) {
                    val loadingAnimator = createLoadingAnimator(graphicOverlay, barcodeInCenter)
                    loadingAnimator.start()
                    graphicOverlay.add(BarcodeLoadingGraphic(graphicOverlay, loadingAnimator))
                    cameraViewModel.setWorkflowState(WorkflowState.SEARCHING)
                } else {
                    cameraViewModel.setWorkflowState(WorkflowState.DETECTED)
                    cameraViewModel.detectedBarcode.setValue(barcodeInCenter)
                }
            }
        }
        graphicOverlay.invalidate()
    }

    private fun createLoadingAnimator(graphicOverlay: GraphicOverlay, barcode: FirebaseVisionBarcode): ValueAnimator {
        val endProgress = 1.1f
        return ValueAnimator.ofFloat(0f, endProgress).apply {
            duration = 2000
            addUpdateListener {
                if ((animatedValue as Float).compareTo(endProgress) >= 0) {
                    graphicOverlay.clear()
                    cameraViewModel.setWorkflowState(WorkflowState.SEARCHED)
                    cameraViewModel.detectedBarcode.setValue(barcode)
                } else {
                    graphicOverlay.invalidate()
                }
            }
        }
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Barcode detection failed!", e)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close barcode detector!", e)
        }
    }

    companion object {
        private const val TAG = "BarcodeProcessor"
    }
}
