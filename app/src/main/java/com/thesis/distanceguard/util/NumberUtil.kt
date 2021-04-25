package com.thesis.distanceguard.util

object NumberUtil {
    fun toNumberWithCommas(input: Long): String {
        return "%,d".format(input)
    }
}