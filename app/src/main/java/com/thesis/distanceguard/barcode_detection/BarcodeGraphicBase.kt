
package com.thesis.distanceguard.barcode_detection


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.thesis.distanceguard.R
import com.thesis.distanceguard.camera_module.GraphicOverlay
import com.thesis.distanceguard.camera_module.GraphicOverlay.Graphic
import com.thesis.distanceguard.util.BarcodePreferenceUtils

internal abstract class BarcodeGraphicBase(overlay: GraphicOverlay) : Graphic(overlay) {

    private val boxPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_reticle_stroke)
        style = Style.STROKE
        strokeWidth = context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_stroke_width).toFloat()
    }

    private val scrimPaint: Paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.barcode_reticle_background)
    }

    private val eraserPaint: Paint = Paint().apply {
        strokeWidth = boxPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    val boxCornerRadius: Float =
        context.resources.getDimensionPixelOffset(R.dimen.barcode_reticle_corner_radius).toFloat()

    val pathPaint: Paint = Paint().apply {
        color =  ContextCompat.getColor(context, R.color.reticle_ripple)
        style = Style.STROKE
        strokeWidth = boxPaint.strokeWidth
        pathEffect = CornerPathEffect(boxCornerRadius)
    }

    val boxRect: RectF = BarcodePreferenceUtils.getBarcodeReticleBox(overlay)

    override fun draw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), scrimPaint)

        eraserPaint.style = Style.FILL
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint)
        eraserPaint.style = Style.STROKE
        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, eraserPaint)

        canvas.drawRoundRect(boxRect, boxCornerRadius, boxCornerRadius, boxPaint)
    }
}
