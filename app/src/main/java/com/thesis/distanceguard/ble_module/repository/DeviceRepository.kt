package com.thesis.distanceguard.ble_module.repository


import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.dao.DeviceDao
import com.thesis.distanceguard.ble_module.dao.DeviceRoomDatabase
import com.thesis.distanceguard.ble_module.util.BluetoothUtils
import com.thesis.distanceguard.ble_module.util.Constants
import com.thesis.distanceguard.ble_module.util.NotificationUtils
import kotlinx.coroutines.GlobalScope

object DeviceRepository {

    private lateinit var deviceDao: DeviceDao
    val currentDevices: MutableLiveData<List<Device>> = MutableLiveData()

    lateinit var allDevices: LiveData<List<Device>>

    fun init(applicationContext: Context) {
        deviceDao = DeviceRoomDatabase.getDatabase(
            applicationContext,
            GlobalScope
        ).deviceDao()
        allDevices = deviceDao.getAllDevices()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(device: Device) {
        deviceDao.insert(device)
        currentDevices.postValue(
            getCurrentDevices()
        )

        if (!device.isTeamMember) {
            val signal = BluetoothUtils.calculateSignal(device.rssi, device.txPower)

            when {
                signal <= Constants.SIGNAL_DISTANCE_OK -> {
                }
                signal <= Constants.SIGNAL_DISTANCE_LIGHT_WARN -> {
                }
                signal <= Constants.SIGNAL_DISTANCE_STRONG_WARN -> {
                    NotificationUtils.sendNotificationDanger()
                }
                else -> {
                    NotificationUtils.sendNotificationTooClose()
                }
            }
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateCurrentDevices() {
        currentDevices.postValue(
            getCurrentDevices()
        )
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun noCurrentDevices() {
        currentDevices.postValue(emptyList())
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll() {
        deviceDao.deleteAllDevices()
    }

    private fun getCurrentDevices(): List<Device> {
        val devices = deviceDao.getCurrentDevicesOrderByRssi(
            System.currentTimeMillis() - (Constants.FOREGROUND_TRACE_INTERVAL * 2),
            System.currentTimeMillis())
        var averagedDevices  = mutableListOf<Device>()
        for (device in devices) {
            val current = averagedDevices.lastOrNull { it.deviceUuid.contentEquals(device.deviceUuid) }
            current?.let {
                it.rssi = (it.rssi + device.rssi).div(2)
                it.txPower = (it.txPower + device.txPower).div(2)
            } ?: averagedDevices.add(device)
        }

        return averagedDevices
    }
}