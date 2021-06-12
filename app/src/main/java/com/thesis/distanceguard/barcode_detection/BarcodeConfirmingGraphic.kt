

package com.thesis.distanceguard.barcode_detection

import android.graphics.Canvas
import android.graphics.Path
import com.thesis.distanceguard.camera_module.GraphicOverlay
import com.thesis.distanceguard.util.BarcodePreferenceUtils
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode

internal class BarcodeConfirmingGraphic(overlay: GraphicOverlay, private val barcode: FirebaseVisionBarcode) :
    BarcodeGraphicBase(overlay) {

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val sizeProgress = BarcodePreferenceUtils.getProgressToMeetBarcodeSizeRequirement(overlay, barcode)
        val path = Path()
        if (sizeProgress > 0.95f) {
            path.moveTo(boxRect.left, boxRect.top)
            path.lineTo(boxRect.right, boxRect.top)
            path.lineTo(boxRect.right, boxRect.bottom)
            path.lineTo(boxRect.left, boxRect.bottom)
            path.close()
        } else {
            path.moveTo(boxRect.left, boxRect.top + boxRect.height() * sizeProgress)
            path.lineTo(boxRect.left, boxRect.top)
            path.lineTo(boxRect.left + boxRect.width() * sizeProgress, boxRect.top)

            path.moveTo(boxRect.right, boxRect.bottom - boxRect.height() * sizeProgress)
            path.lineTo(boxRect.right, boxRect.bottom)
            path.lineTo(boxRect.right - boxRect.width() * sizeProgress, boxRect.bottom)
        }
        canvas.drawPath(path, pathPaint)
    }
}
