package com.thesis.distanceguard.ble_module.core

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseSettings
import android.util.Log

object BLEAdvertiseCallbackImpl: AdvertiseCallback() {
    const val TAG = "BLEServerAdCallback"

    override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
        Log.d(TAG,"Peripheral advertising started.")
    }


    override fun onStartFailure(errorCode: Int) {
        Log.e(TAG,"Peripheral advertising failed: $errorCode")
    }
}