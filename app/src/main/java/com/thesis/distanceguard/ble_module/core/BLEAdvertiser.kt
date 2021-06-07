package com.thesis.distanceguard.ble_module.core


import android.app.AlarmManager
import android.app.PendingIntent
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattService
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.ParcelUuid
import android.os.PowerManager
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import com.thesis.distanceguard.ble_module.*
import com.thesis.distanceguard.ble_module.BLEController.getAlarmManager
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_ID
import com.thesis.distanceguard.ble_module.util.Constants.MANUFACTURER_SUBSTRING
import com.thesis.distanceguard.ble_module.util.Constants.BACKGROUND_TRACE_INTERVAL
import timber.log.Timber
import java.nio.charset.StandardCharsets
import java.util.*



class BLEAdvertiser : BroadcastReceiver()  {
    private val TAG = "BLEAdvertiser"
    private val WAKELOCK_TAG = "com:thesis:distanceguard:ble_module:core:BLEAdvertiser"
    private val INTERVAL_KEY = "interval"
    private val SERVER_REQUEST_CODE = 10
    private val START_DELAY = 10

    lateinit var appContext: Context



    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive")
        val interval = intent.getIntExtra(INTERVAL_KEY, BACKGROUND_TRACE_INTERVAL)
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_TAG)
        wl.acquire(interval.toLong())
        synchronized(BLEController) {
            // Chain the next alarm...
            appContext = context.applicationContext
            BLEController.init(appContext)
            next(interval)
            if (BLEController.isEnabled()) {
                setupServer()
                startAdvertising(object :AdvertiseCallback(){}, BLEController.deviceNameServiceUuid)

            }
        }
        wl.release()
    }


    fun next(interval: Int) {
        val alarmManager = getAlarmManager(appContext)
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            getPendingIntent(interval, appContext))
    }


    fun enable(interval: Int, context: Context) {
        this.appContext = context.applicationContext
        val alarmManager = getAlarmManager(appContext)
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + START_DELAY,
            getPendingIntent(interval, appContext))
    }



    fun disable(interval: Int, context: Context) {
        synchronized (BLEController) {
            this.appContext = context.applicationContext
            val alarmManager = getAlarmManager(appContext)

            alarmManager.cancel(getPendingIntent(interval, appContext))

            stopAdvertising()
        }
    }


    private fun getPendingIntent(interval: Int, context: Context) : PendingIntent {
        val intent = Intent(context, BLEAdvertiser::class.java)
        intent.putExtra(INTERVAL_KEY, interval)
        return PendingIntent.getBroadcast(context, SERVER_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }



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


    private fun stopAdvertising() {
        synchronized(this) {
            try {
                BLEController.bluetoothGattServer?.let { stopServer(it) }
                Timber.d("<<<<<<<<<<BLE Beacon Forced Stopped")
            }catch (exception: Exception) {
                val msg = " ${exception::class.qualifiedName} while stopping advertising caused by ${exception.localizedMessage}"
                Log.e(TAG, msg)
            }
        }
    }


}