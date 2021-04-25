package com.thesis.distanceguard.util

import java.text.SimpleDateFormat
import java.util.*

object AppUtil {
    private const val DATE_FORMAT = "MM/dd/yyyy"

    fun toNumberWithCommas(input: Long): String {
        return "%,d".format(input)
    }

    fun convertMillisecondsToDateFormat(milliSeconds: Long): String? {
        val calendar: Calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat(DATE_FORMAT)
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}