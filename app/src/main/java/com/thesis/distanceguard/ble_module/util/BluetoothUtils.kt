package com.thesis.distanceguard.ble_module.util


object BluetoothUtils {

    fun calculateSignal(rssi: Int, txPower: Int): Int {
        val adjustedTxPower = if (txPower + rssi < 0) Constants.ASSUMED_TX_POWER else txPower

        return adjustedTxPower + rssi
    }

}