package com.thesis.distanceguard.util

import java.text.SimpleDateFormat
import java.util.*

object AppUtil {
    private const val DATE_FORMAT = "MMMM dd, yyyy HH:mm:ss"

    fun toNumberWithCommas(input: Long): String {
        return "%,d".format(input)
    }

    fun convertMillisecondsToDateFormat(milliSeconds: Long): String? {
        val calendar: Calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat(DATE_FORMAT,Locale.US)
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    // Pair<String,Long> -> Pair<String,Float>
    fun convertPairLongToPairFloat(currentList: List<Pair<String, Long>>): List<Pair<String, Float>> {
        val newList = ArrayList<Pair<String, Float>>()
        for (i in 1 until currentList.size) {
            val keySecond = currentList[i].second.toFloat()
            val newPair = Pair(currentList[i].first, keySecond)
            newList.add(newPair)
        }
        return newList
    }
}