package com.thesis.distanceguard.util

import com.thesis.distanceguard.camera_module.CameraSizePair
import com.thesis.distanceguard.camera_module.GraphicOverlay
import android.content.Context
import android.graphics.RectF
import androidx.annotation.StringRes
import com.google.android.gms.common.images.Size
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode



object BarcodePreferenceUtils {


    fun getProgressToMeetBarcodeSizeRequirement(
        overlay: GraphicOverlay,
        barcode: FirebaseVisionBarcode
    ): Float {
        val context = overlay.context
        return 1f
    }

    fun getBarcodeReticleBox(overlay: GraphicOverlay): RectF {
        val context = overlay.context
        val overlayWidth = overlay.width.toFloat()
        val overlayHeight = overlay.height.toFloat()
        val boxWidth = overlayWidth * 80 / 100
        val boxHeight = overlayHeight * 35 / 100
        val cx = overlayWidth / 2
        val cy = overlayHeight / 2
        return RectF(cx - boxWidth / 2, cy - boxHeight / 2, cx + boxWidth / 2, cy + boxHeight / 2)
    }

    fun shouldDelayLoadingBarcodeResult(context: Context): Boolean {
        return true
    }


    fun getUserSpecifiedPreviewSize(context: Context): CameraSizePair? {
        return try {
            CameraSizePair(
                Size.parseSize(null),
                Size.parseSize(null))
        } catch (e: Exception) {
            null
        }
    }


}
