
package com.thesis.distanceguard.util


import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.Log
import com.thesis.distanceguard.camera_module.CameraSizePair
import java.util.ArrayList
import kotlin.math.abs

object BarcodeUtils {


    const val ASPECT_RATIO_TOLERANCE = 0.01f

    private const val TAG = "Utils"



    fun isPortraitMode(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT


    fun generateValidPreviewSizeList(camera: Camera): List<CameraSizePair> {
        val parameters = camera.parameters
        val supportedPreviewSizes = parameters.supportedPreviewSizes
        val supportedPictureSizes = parameters.supportedPictureSizes
        val validPreviewSizes = ArrayList<CameraSizePair>()
        for (previewSize in supportedPreviewSizes) {
            val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()


            for (pictureSize in supportedPictureSizes) {
                val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()
                if (abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                    validPreviewSizes.add(CameraSizePair(previewSize, pictureSize))
                    break
                }
            }
        }


        if (validPreviewSizes.isEmpty()) {
            Log.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size.")
            for (previewSize in supportedPreviewSizes) {
                validPreviewSizes.add(CameraSizePair(previewSize, null))
            }
        }
        return validPreviewSizes
    }

}
