package com.thesis.distanceguard.ble_module.core

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Handler
import android.os.ParcelUuid
import android.util.Log
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.util.Constants
import timber.log.Timber


object ScanCallbackImpl: ScanCallback() {
    private val TAG = "BtleScanCallback"
    val mScanResults = HashMap<String, Device>()
    val handler = Handler()



    override fun onScanResult(
        callbackType: Int,
        result: ScanResult
    ) {
        Timber.d("BtleScanCallback onScanResult")
        addScanResult(result)
    }


    override fun onBatchScanResults(results: List<ScanResult>) {
        for (result in results) {
            addScanResult(result)
        }
    }



    override fun onScanFailed(errorCode: Int) {
        Log.e(TAG, "BLE Scan Failed with code $errorCode")
    }



     fun addScanResult(result: ScanResult) {
        synchronized(this) {
            val deviceAddress = result.device.address
            var uuid: ParcelUuid? = result.scanRecord?.serviceUuids?.get(0)


            // Only record devices where the UUID is one from our app...
            if ((uuid.toString().startsWith(Constants.SERVICE_PREFIX))) {
                var rssi: Int = result.rssi
                var txPower: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    result.txPower
                } else {
                    -1
                }

                var timeStampNanos: Long = result.timestampNanos
                val timeStamp: Long = System.currentTimeMillis()
                var sessionId = deviceAddress

                var newDevice = Device(
                    uuid.toString(),
                    rssi,
                    txPower,
                    timeStampNanos,
                    timeStamp,
                    sessionId,
                    BLEController.isTeamMember(uuid.toString())
                )

                // Use a rolling average...
                if (mScanResults.containsKey(deviceAddress)) {
                    newDevice.rssi = (newDevice.rssi + mScanResults[deviceAddress]!!.rssi).div(2)
                    newDevice.txPower =
                        (newDevice.txPower + mScanResults[deviceAddress]!!.txPower).div(2)
                }
                mScanResults[deviceAddress] = newDevice
            }
        }
    }
}