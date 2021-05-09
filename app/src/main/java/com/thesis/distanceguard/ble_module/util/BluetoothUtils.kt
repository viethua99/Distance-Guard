package com.thesis.distanceguard.ble_module.util

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import com.thesis.distanceguard.ble_module.util.Constants.SERVICE_STRING

/**
 * Some utilities to help us with various Bluetooth things.
 */
object BluetoothUtils {

    /**
     * find a characteristic inside the GATT
     *
     * @param bluetoothGatt The GATT to search
     * @param uuidString The UUID of the characteristic we want to find
     * @return The characteristic
     */
    fun findCharacteristic(
        bluetoothGatt: BluetoothGatt,
        uuidString: String
    ): BluetoothGattCharacteristic? {
        val serviceList = bluetoothGatt.services
        val service = findService(serviceList) ?: return null
        val characteristicList =
            service.characteristics
        for (characteristic in characteristicList) {
            if (characteristicMatches(characteristic, uuidString)) {
                return characteristic
            }
        }
        return null
    }

    /**
     * A helper function for checking matches.
     */
    private fun characteristicMatches(
        characteristic: BluetoothGattCharacteristic?,
        uuidString: String
    ): Boolean {
        if (characteristic == null) {
            return false
        }
        val uuid = characteristic.uuid
        return uuidMatches(uuid.toString(), uuidString)
    }


    /**
     * Checks to see if the characteristic needs a response
     *
     * @param characteristic the characteristic to check
     * @return True if they match
     */
    fun requiresResponse(characteristic: BluetoothGattCharacteristic): Boolean {
        return (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                != BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)
    }

    /**
     * Checks to see if the characteristic needs a confirmation
     *
     * @param characteristic the characteristic to check
     * @return True if they match
     */
    fun requiresConfirmation(characteristic: BluetoothGattCharacteristic): Boolean {
        return (characteristic.properties and BluetoothGattCharacteristic.PROPERTY_INDICATE
                == BluetoothGattCharacteristic.PROPERTY_INDICATE)
    }


    /**
     * Calculates the strength of the signal from another handset taking into consideration
     * the type of handset and the reported transmission power.
     *
     * @param rssi The RSSI reported from the scan
     * @param txPower The transmission power reported from the scan
     * @return A signal strength
     */
    fun calculateSignal(rssi: Int, txPower: Int): Int {
        // Fix for older handset that don't report power...
        val adjustedTxPower = if (txPower + rssi < 0) Constants.ASSUMED_TX_POWER else txPower

        // Notify the user when we are adding a device that's too close

        return adjustedTxPower + rssi
    }


    /**
     * Check and see if this service string matches the one in the constants
     *
     * @param serviceIdString The service string to check
     * @return True if if matches the one in the constants
     */
    private fun matchesServiceUuidString(serviceIdString: String): Boolean {
        return uuidMatches(serviceIdString, SERVICE_STRING)
    }

    /**
     * find the service
     *
     * @param serviceList The service list from the GATT
     * @return The service that matches
     */
    fun findService(serviceList: List<BluetoothGattService>): BluetoothGattService? {
        for (service in serviceList) {
            val serviceIdString = service.uuid
                .toString()
            if (matchesServiceUuidString(serviceIdString)) {
                return service
            }
        }
        return null
    }

    /**
     * a simple matching function for UUID's
     *
     * @param uuidString The UUID String to match
     * @param matches The match
     * @return True if they match
     */
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