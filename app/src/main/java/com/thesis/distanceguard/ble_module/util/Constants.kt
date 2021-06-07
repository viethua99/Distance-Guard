package com.thesis.distanceguard.ble_module.util


object Constants {
    const val PREF_TEAM_IDS = "team_ids"
    const val PREF_UNIQUE_ID = "unique_uuid"
    const val PREF_IS_PAUSED = "is_paused"
    const val PREF_FILE_NAME = "com.thesis.distanceguard.preferencesV2"

    const val MANUFACTURER_SUBSTRING = "9b4"
    const val MANUFACTURER_SUBSTRING_MASK = "000"
    const val MANUFACTURER_ID = 1023

    const val SERVICE_STRING = "d2b99ccb-263e-3ac6-838b-0d00b1f549ed"
    var SERVICE_PREFIX = SERVICE_STRING.subSequence(0..7 )


    const val SCAN_PERIOD: Long = 4000
    const val REBROADCAST_PERIOD = 10000
    const val BACKGROUND_TRACE_INTERVAL = 10000
    const val FOREGROUND_TRACE_INTERVAL = 10000

    const val SIGNAL_DISTANCE_OK = 23 // This or lower is socially distant = green
    const val SIGNAL_DISTANCE_LIGHT_WARN = 37 // This to SIGNAL_DISTANCE_OK warning = yellow
    const val SIGNAL_DISTANCE_STRONG_WARN = 52 // This to SIGNAL_DISTANCE_LIGHT_WARN strong warning = orange
    // ...and above this is not socially distant = red

    const val ASSUMED_TX_POWER = 127
}