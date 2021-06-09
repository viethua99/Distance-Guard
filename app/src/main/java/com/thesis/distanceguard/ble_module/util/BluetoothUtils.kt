package com.thesis.distanceguard.ble_module.util

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.thesis.distanceguard.ble_module.util.Constants.SERVICE_STRING

object BluetoothUtils {



    fun calculateSignal(rssi: Int, txPower: Int): Int {
        // Fix for older handset that don't report power...
        val adjustedTxPower = if (txPower + rssi < 0) Constants.ASSUMED_TX_POWER else txPower

        // Notify the user when we are adding a device that's too close

        return adjustedTxPower + rssi
    }



    private fun matchesServiceUuidString(serviceIdString: String): Boolean {
        return uuidMatches(serviceIdString, SERVICE_STRING)
    }



    private fun uuidMatches(
        uuidString: String,
        vararg matches: String
    ): Boolean {
        for (match in matches) {
            if (uuidString.equals(match, ignoreCase = true)) {
                return true
            }
        }
        return false
    }
}