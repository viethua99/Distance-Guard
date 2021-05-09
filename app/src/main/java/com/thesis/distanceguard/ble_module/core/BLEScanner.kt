package com.thesis.distanceguard.ble_module.core

import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.repository.DeviceRepository
import com.thesis.distanceguard.ble_module.util.Constants
import com.thesis.distanceguard.ble_module.util.Constants.ANDROID_MANUFACTURE_ID
import com.thesis.distanceguard.ble_module.util.Constants.ANDROID_MANUFACTURE_SUBSTRING
import com.thesis.distanceguard.ble_module.util.Constants.ANDROID_MANUFACTURE_SUBSTRING_MASK
import com.thesis.distanceguard.ble_module.util.Constants.APPLE_DEVICE_NAME
import com.thesis.distanceguard.ble_module.util.Constants.SCAN_PERIOD
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

/**
 * This code implements detection of other devices.  When another device is detected we store the
 * detection in a database.  That lets this library run independently of the rest of the application.
 * In order to support applications written in React and other cross compatible platforms it's possible
 * to provide a callback and have the detection dealt with some other way (e.g. writing to some
 * cross platform database)
 *
 */
class BLEScanner : BroadcastReceiver() {
    private val TAG = "BLEClient"
    private val WAKELOCK_TAG = "ai:kun:socialdistancealarm:worker:BLEClient"

    private val INTERVAL_KEY = "interval"
    private val ISREACTNATIVE_KEY = "isReactNative"

    private val RSSI_KEY = "rssi"
    private val UUID_KEY = "uuid"

    private val CLIENT_REQUEST_CODE = 11
    private val START_DELAY = 10

    private var mScanning = false
    private var mConnected = false

    /**
     * We use alarm manager and cheat by scheduling a new alarm every time the alarm goes off.
     * I know the code seems overly complex, but sadly different versions of Android behaved slightly
     * differently and without all of the below code there were crashes.
     *
     * @param context The context we are using to deal with the alarm
     * @param intent The intent used to launch
     */
    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        val interval = intent.getIntExtra(INTERVAL_KEY, Constants.BACKGROUND_TRACE_INTERVAL)
        val isReactNative = intent.getBooleanExtra(ISREACTNATIVE_KEY, false)
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
        wl.acquire(interval.toLong())
        synchronized(BLEController) {
            // Chain the next alarm...
            BLEController.init(context.applicationContext, isReactNative)
            next(interval, context.applicationContext)
            if (BLEController.isEnabled()) startScan(context.applicationContext)
        }
        wl.release()
    }

    /**
     * set up the next alarm manager
     *
     * @param interval The interval in MS
     * @param context The context to use
     */
    fun next(interval: Int, context: Context) {
        val alarmManager = BLEController.getAlarmManager(context)
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            getPendingIntent(interval, context))

    }

    /**
     * Enable scanning
     *
     * @param interval The interval at which to restart scanning
     * @param context The context to use
     */
    fun enable(interval: Int, context: Context) {
        val alarmManager = BLEController.getAlarmManager(context)
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + START_DELAY,
            getPendingIntent(interval, context)
        )
    }

    /**
     * Disable scanning
     *
     * @param interval The interval at which scanning was started.
     *                 This is needed so that the alarm can be found.
     * @param context The context to use.
     */
    fun disable(interval: Int, context: Context) {
        synchronized(BLEController) {
            BLEController.getAlarmManager(context).cancel(getPendingIntent(interval, context))
            stopScan(context)
        }
    }

    /**
     * create the pending intent that will be fired
     *
     * @param interval the interval for the event
     * @param context the context to use
     * @return the intent
     */
    private fun getPendingIntent(interval: Int, context: Context): PendingIntent {
        val intent = Intent(context, BLEScanner::class.java)
        intent.putExtra(INTERVAL_KEY, interval)
        intent.putExtra(ISREACTNATIVE_KEY, BLEController.isReactNative)
        return PendingIntent.getBroadcast(
            context,
            CLIENT_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }


    /**
     * Start scanning
     *
     * @param context the context to use
     */
    private fun startScan(context: Context) {
        if (mScanning) {
            Log.w(TAG,"Already scanning")
            return
        }

        val androidScanFilter = ScanFilter.Builder()
            .setManufacturerData(ANDROID_MANUFACTURE_ID,
                            ANDROID_MANUFACTURE_SUBSTRING.toByteArray(StandardCharsets.UTF_8),
                            ANDROID_MANUFACTURE_SUBSTRING_MASK.toByteArray(StandardCharsets.UTF_8))
            .build()
        val appleScanFilter = ScanFilter.Builder()
            .setDeviceName(APPLE_DEVICE_NAME)
            .build()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            BLEController.bluetoothLeScanner!!.startScan(listOf(androidScanFilter, appleScanFilter), settings, ScanCallbackImpl)
            ScanCallbackImpl.handler.postDelayed(Runnable { stopScan(context) }, SCAN_PERIOD)
            mScanning = true
            Log.d(TAG, "+++++++Started scanning.")
        } catch (exception: Exception) {
            val msg = " ${exception::class.qualifiedName} while starting scanning caused by ${exception.localizedMessage}"
            Log.e(TAG, msg)
        }
    }

    /**
     * stop scanning
     *
     * @param context the context to use
     */
    private fun stopScan(context: Context) {

        synchronized(BLEController) {
            try {
                if (mScanning && BLEController.bluetoothManager!!.adapter.isEnabled) {
                    BLEController.bluetoothLeScanner!!.stopScan(ScanCallbackImpl)
                    scanComplete(context)
                }

            } catch (exception: Exception) {
                val msg = " ${exception::class.qualifiedName} while stopping scanning caused by ${exception.localizedMessage}"
                Log.e(TAG, msg)
            }
            mScanning = false
        }
        Log.d(TAG, "-------Stopped scanning.")
    }

    private fun scanComplete(context: Context) {
        if (BLEController.isReactNative ) {
            val devices = ScanCallbackImpl.mScanResults.values
            Intent().also {  intent ->
                intent.action = Constants.INTENT_DEVICE_SCANNED
                intent.putExtra(RSSI_KEY, devices.map { it.rssi }.toIntArray())
                val ids = devices.map { it.deviceUuid }
                intent.putExtra(UUID_KEY, ids.toTypedArray())
                Log.i(TAG, "Sending ids ${ids.size}")
                context.sendBroadcast(intent)
            }
            ScanCallbackImpl.mScanResults.clear()
            return
        }

        var noCurrentDevices = true

        if (ScanCallbackImpl.mScanResults.isNotEmpty()) {
            for (deviceAddress in ScanCallbackImpl.mScanResults.keys) {
                val result: Device? = ScanCallbackImpl.mScanResults.get(deviceAddress)
                result?.let { device ->
                    Log.d(
                        TAG,
                        "+++++++++++++ Traced: device=${device.deviceUuid} rssi=${device.rssi} txPower=${device.txPower} timeStampNanos=${device.timeStampNanos} timeStamp=${device.timeStamp} sessionId=${device.sessionId} +++++++++++++"
                    )


                    // If the library was run from a native app use the built-in Device Repository
                    GlobalScope.launch { DeviceRepository.insert(device) }

                    noCurrentDevices = false
                }
            }

            // Clear the scan results
            ScanCallbackImpl.mScanResults.clear()
        }
        if (noCurrentDevices) {
            GlobalScope.launch { DeviceRepository.noCurrentDevices() }
        }


    }
}