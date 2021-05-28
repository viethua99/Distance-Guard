package com.thesis.distanceguard.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.thesis.distanceguard.R
import kotlinx.android.synthetic.main.custom_marker_view.view.*


@SuppressLint("ViewConstructor")
class MyMarkerView(context: Context?, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private val clMarker: ConstraintLayout = findViewById(R.id.cl_marker)

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val tvTime: TextView = findViewById(R.id.tvTime)

    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        highlight?.let {
            tvTime.text = "Time: " +  AppUtil.convertMillisecondsToShortDateFormat(it.x.toLong())

            when(highlight.dataSetIndex){
                0 -> {
                    clMarker.background = ContextCompat.getDrawable(context, R.drawable.blue_rounded_corner_button)
                    tvContent.text = "Cases: ${AppUtil.toNumberWithCommas(it.y.toLong())}"
                }

                1-> {
                    clMarker.background = ContextCompat.getDrawable(context, R.drawable.green_rounded_corner_button)
                    tvContent.text = "Recovered: ${AppUtil.toNumberWithCommas(it.y.toLong())}"

                }
                else -> {
                    clMarker.background = ContextCompat.getDrawable(context, R.drawable.red_rounded_corner_button)
                    tvContent.text = "Deaths: ${AppUtil.toNumberWithCommas(it.y.toLong())}"
                }
            }
        }

        super.refreshContent(entry, highlight)

    }

    override fun draw(canvas: Canvas?, posX: Float, posY: Float) {
        super.draw(canvas, posX, posY)
        getOffsetForDrawingAtPoint(posX,posY)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}