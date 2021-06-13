package com.thesis.distanceguard.ble_module.core


import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.os.ParcelUuid
import android.util.Log
import com.thesis.distanceguard.ble_module.*
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_ID
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_SUBSTRING
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.util.*



class BLEAdvertiser  {
    private val TAG = "BLEAdvertiser"

    private fun setupServer() {
        try {
            if (BLEController.bluetoothGattServer!!.getService(
                    BLEController.deviceNameServiceUuid) == null) {
                val deviceService = BluetoothGattService(
                    BLEController.deviceNameServiceUuid,
                    BluetoothGattService.SERVICE_TYPE_PRIMARY
                )
                BLEController.bluetoothGattServer!!.addService(deviceService)
            }
        } catch (exception: Exception) {
            val msg = " ${exception::class.qualifiedName} while setting up the server caused by ${exception.localizedMessage}"
            Log.e(TAG, msg)
        }
    }

    fun startAdvertising(){
        setupServer()
        startAdvertising(BLEAdvertiseCallbackImpl, BLEController.deviceNameServiceUuid)
    }


    private fun stopServer(gattServer: BluetoothGattServer) {
        gattServer.close()
        Timber.d("server closed.")
    }



    private fun startAdvertising(callback: AdvertiseCallback, uuid: UUID) {
        try {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setConnectable(false)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(true)
                .addManufacturerData(
                    MANUFACTURER_ID,
                    MANUFACTURER_SUBSTRING.toByteArray(StandardCharsets.UTF_8)
                )
                .addServiceUuid(ParcelUuid(uuid))
                .build()

            BLEController.bluetoothLeAdvertiser!!.stopAdvertising(callback)
            BLEController.bluetoothLeAdvertiser!!.startAdvertising(settings, data, callback)
            Log.d(TAG, ">>>>>>>>>>BLE Beacon Started UUID: $uuid")
        } catch (exception: Exception) {
            val msg = " ${exception::class.qualifiedName} while starting advertising caused by ${exception.localizedMessage}"
            Log.e(TAG, msg)
        }
    }


     fun stopAdvertising() {
        synchronized(this) {
            try {
                BLEController.bluetoothLeAdvertiser?.stopAdvertising(BLEAdvertiseCallbackImpl)
                BLEController.bluetoothGattServer?.let { stopServer(it) }
                Timber.d("<<<<<<<<<<BLE Beacon Forced Stopped")
            }catch (exception: Exception) {
                val msg = " ${exception::class.qualifiedName} while stopping advertising caused by ${exception.localizedMessage}"
                Log.e(TAG, msg)
            }
        }
    }


}