
package com.thesis.distanceguard.barcode_detection

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.thesis.distanceguard.R
import com.thesis.distanceguard.camera_module.GraphicOverlay
import com.thesis.distanceguard.camera_module.CameraReticleAnimator



internal class BarcodeReticleGraphic(overlay: GraphicOverlay, private val animator: CameraReticleAnimator) :
    BarcodeGraphicBase(overlay) {

    private val ripplePaint: Paint
    private val rippleSizeOffset: Int
    private val rippleStrokeWidth: Int
    private val rippleAlpha: Int

    init {
        val resources = overlay.resources
        ripplePaint = Paint()
        ripplePaint.style = Style.STROKE
        ripplePaint.color = ContextCompat.getColor(context, R.color.reticle_ripple)
        rippleSizeOffset = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_size_offset)
        rippleStrokeWidth = resources.getDimensionPixelOffset(R.dimen.barcode_reticle_ripple_stroke_width)
        rippleAlpha = ripplePaint.alpha
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        ripplePaint.alpha = (rippleAlpha * animator.rippleAlphaScale).toInt()
        ripplePaint.strokeWidth = rippleStrokeWidth * animator.rippleStrokeWidthScale
        val offset = rippleSizeOffset * animator.rippleSizeScale
        val rippleRect = RectF(
                boxRect.left - offset,
                boxRect.top - offset,
                boxRect.right + offset,
                boxRect.bottom + offset
        )
        canvas.drawRoundRect(rippleRect, boxCornerRadius, boxCornerRadius, ripplePaint)
    }
}
