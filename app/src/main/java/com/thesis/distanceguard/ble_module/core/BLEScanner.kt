package com.thesis.distanceguard.ble_module.core

import android.bluetooth.le.*

import android.os.Handler
import android.util.Log
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.repository.DeviceRepository
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_ID
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_SUBSTRING
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_SUBSTRING_MASK
import com.thesis.distanceguard.ble_module.util.Constants.CHECK_PERIOD
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.nio.charset.StandardCharsets



class BLEScanner {
    private val TAG = "BLEScanner"

    private var mScanning = false

     fun startScan() {
        if (mScanning) {
            Log.w(TAG,"Already scanning")
            return
        }

        val androidScanFilter = ScanFilter.Builder()
            .setManufacturerData(MANUFACTURER_ID,
                            MANUFACTURER_SUBSTRING.toByteArray(StandardCharsets.UTF_8),
                            MANUFACTURER_SUBSTRING_MASK.toByteArray(StandardCharsets.UTF_8))
            .build()


        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            BLEController.bluetoothLeScanner!!.startScan(listOf(androidScanFilter), settings, scanCallback)
            mScanning = true
            Log.d(TAG, "+++++++Started scanning.")
        } catch (exception: Exception) {
            val msg = " ${exception::class.qualifiedName} while starting scanning caused by ${exception.localizedMessage}"
            Log.e(TAG, msg)
        }
    }


     fun stopScan() {

        synchronized(BLEController) {
            try {
                if (mScanning && BLEController.bluetoothManager!!.adapter.isEnabled) {
                    Timber.d("stopScan")
                    BLEController.bluetoothLeScanner!!.stopScan(scanCallback)
                    checkResultList()
                    GlobalScope.launch {
                        DeviceRepository.deleteAll()
                    }

                    stopHandler()
                   isHandlerStarted = false
                }

            } catch (exception: Exception) {
                val msg = " ${exception::class.qualifiedName} while stopping scanning caused by ${exception.localizedMessage}"
                Log.e(TAG, msg)
            }
            mScanning = false
        }
        Log.d(TAG, "-------Stopped scanning.")
    }

    private val taskHandler: Handler = Handler()
    private var isHandlerStarted = false


    private fun startHandler() {
        taskHandler.postDelayed(repeatableTaskRunnable, CHECK_PERIOD)
    }

    private fun stopHandler() {
        taskHandler.removeCallbacks(repeatableTaskRunnable)
    }

    private val repeatableTaskRunnable = Runnable {
        Timber.d("repeat")
        checkResultList()
        startHandler()
    }



    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Timber.d("onScanResult")
            result?.let {
                if(!isHandlerStarted){ //Start handler only one time
                    startHandler()
                    isHandlerStarted = true
                }
                ScanCallbackImpl.addScanResult(it)

            }

        }
    }
    private fun checkResultList() {
        Timber.d("checkResultList")
        var noCurrentDevices = true

        if (ScanCallbackImpl.mScanResults.isNotEmpty()) {
            for (deviceAddress in ScanCallbackImpl.mScanResults.keys) {
                val result: Device? = ScanCallbackImpl.mScanResults.get(deviceAddress)
                result?.let { device ->
                    Log.d(
                        TAG,
                        "+++++++++++++ Traced: device=${device.deviceUuid} rssi=${device.rssi} txPower=${device.txPower} timeStampNanos=${device.timeStampNanos} timeStamp=${device.timeStamp} sessionId=${device.sessionId} +++++++++++++"
                    )


                    GlobalScope.launch { DeviceRepository.insert(device) }

                    noCurrentDevices = false
                }
            }

            ScanCallbackImpl.mScanResults.clear()
        }

        if (noCurrentDevices) {
            GlobalScope.launch { DeviceRepository.noCurrentDevices() }
        }


    }
}