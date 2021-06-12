package com.thesis.distanceguard.ble_module.util


object Constants {
    const val PREF_TEAM_IDS = "team_ids"
    const val PREF_UNIQUE_ID = "unique_uuid"
    const val PREF_FILE_NAME = "com.thesis.distanceguard.preferencesV2"

    const val MANUFACTURER_SUBSTRING = "9b4"
    const val MANUFACTURER_SUBSTRING_MASK = "000"
    const val MANUFACTURER_ID = 1023

    const val SERVICE_STRING = "d2b99ccb-263e-3ac6-838b-0d00b1f549ed"
    var SERVICE_PREFIX = SERVICE_STRING.subSequence(0..7 )


    const val CHECK_PERIOD: Long = 2000
    const val FOREGROUND_TRACE_INTERVAL = 10000

    const val SIGNAL_DISTANCE_OK = 23
    const val SIGNAL_DISTANCE_LIGHT_WARN = 37
    const val SIGNAL_DISTANCE_STRONG_WARN = 52


    const val ASSUMED_TX_POWER = 127
}