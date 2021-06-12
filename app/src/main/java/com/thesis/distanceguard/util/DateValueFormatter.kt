package com.thesis.distanceguard.util

import com.github.mikephil.charting.components.AxisBase

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*


class DateValueFormatter : ValueFormatter() {

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val formatter = SimpleDateFormat("MM/dd",Locale.US)

        return formatter.format(value.toLong())
    }
}