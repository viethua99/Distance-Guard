package com.thesis.distanceguard.ble_module


import android.app.AlarmManager
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import android.util.Log
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.thesis.distanceguard.ble_module.core.BLEAdvertiser
import com.thesis.distanceguard.ble_module.core.BLEScanner
import com.thesis.distanceguard.ble_module.repository.DeviceRepository
import com.thesis.distanceguard.ble_module.util.Constants
import com.thesis.distanceguard.ble_module.util.Constants.PREF_FILE_NAME
import com.thesis.distanceguard.ble_module.util.Constants.PREF_IS_PAUSED
import com.thesis.distanceguard.ble_module.util.Constants.PREF_TEAM_IDS
import com.thesis.distanceguard.ble_module.util.Constants.PREF_UNIQUE_ID
import com.thesis.distanceguard.ble_module.util.NotificationUtils
import timber.log.Timber
import java.util.*
import kotlin.collections.HashSet




object BLEController {
    private val mBLEAdvertiser: BLEAdvertiser =
        BLEAdvertiser()
    private val mBLEScanner: BLEScanner =
        BLEScanner()
    private val TAG = "BLEController"

    private var isInit = false
    private lateinit var context: Context
    var bluetoothGattServer: BluetoothGattServer? = null
    var bluetoothManager: BluetoothManager? = null
    var bluetoothLeScanner: BluetoothLeScanner? = null
    var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null


    // Live data you can use to see if things have been started
    val isStarted: MutableLiveData<Boolean> = MutableLiveData(false)

    // A thread safe way to check if things are paused
    var isPaused: Boolean = true
        get() {
            synchronized(this) {
             return field
            }
        }
        set(value) {
            synchronized(this) {
               field = value
                if (value) {
                    stopBLEScan()
                } else {
                    startBLEScan()
                }
            }
        }

    // A thread safe way to set the UUID used by the device
    var uuidString: String?
        get() {
            synchronized(this) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_FILE_NAME, Context.MODE_PRIVATE
                )
                return sharedPrefs.getString(PREF_UNIQUE_ID, null)
            }
        }
        set(value) {
            synchronized(this) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_FILE_NAME, Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPrefs.edit()
                if (value != null) {
                    editor.putString(PREF_UNIQUE_ID, value)
                    editor.commit()
                    init(context)
                    deviceNameServiceUuid = UUID.fromString(value)
                } else {
                    editor.remove(PREF_UNIQUE_ID)
                    editor.commit()
                    stopBLEScan()
                }
            }
        }

    // A thread safe way to set a list of UUID's to mark as team members when they are detected
    var teamUuids: Set<String>?
        get() {
            synchronized(this) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_FILE_NAME, Context.MODE_PRIVATE
                )
                return sharedPrefs.getStringSet(PREF_TEAM_IDS, HashSet<String>())
            }
        }
        set(value) {
            synchronized(this) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_FILE_NAME, Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPrefs.edit()
                if (value != null) {
                    editor.putStringSet(PREF_TEAM_IDS, value)
                    editor.commit()
                } else {
                    editor.remove(PREF_TEAM_IDS)
                    editor.commit()
                }
            }
        }



    fun leaveTeam() {
        synchronized(this) {
            teamUuids = null
            uuidString =
                getNewUniqueId()
            isStarted.value?.let {
                if (it) {
                    stopBLEScan()
                    startBLEScan()
                }
            }
        }
    }



    fun isTeamMember(scannnedUuid: String): Boolean {
        synchronized(this) {
            return teamUuids?.let {
                it.toTypedArray().contains(scannnedUuid)
            } ?: false
        }
    }




    fun getNewUniqueId(): String {
        val stringChars = (('0'..'9') + ('a'..'f')).toList().toTypedArray()
        val id = "0${((1..11).map { stringChars.random() }.joinToString(""))}"
        return Constants.SERVICE_STRING.replaceAfterLast('-', id)
    }

    lateinit var deviceNameServiceUuid: UUID



    fun startBLEScan() {
        Timber.d("start")
        synchronized(this) {
            if (isStarted.value!!) stopBLEScan()
            startForeground()
        }
    }



    fun stopBLEScan() {
        synchronized(this) {
          stopForeground()
        }
    }



    private fun startForeground() {
        if (isEnabled() && !isPaused) {
            Log.i(TAG, "startForeground")
            isStarted.postValue(true)
            mBLEAdvertiser.enable(
                Constants.REBROADCAST_PERIOD,
                context
            )
            mBLEScanner.enable(
                Constants.FOREGROUND_TRACE_INTERVAL,
                context
            )
        } else {
            isStarted.postValue(false)
        }
    }






    private fun stopForeground() {
        if (isEnabled()) {
            mBLEAdvertiser.disable(
                Constants.REBROADCAST_PERIOD,
                context
            )
            mBLEScanner.disable(
                Constants.FOREGROUND_TRACE_INTERVAL,
                context
            )
        }
        isStarted.postValue(false)
    }





    fun isEnabled(): Boolean {
        bluetoothManager?.let {
            if (uuidString == null || it.adapter == null || !it.adapter.isEnabled()) return false
        }

        if (!isInit) init(context) // If bluetooth was off we need to complete the init

        return isInit  // && isLocationEnabled() Location doesn't need to be on
    }




    fun init(applicationContext: Context) {
        synchronized(this) {
            context = applicationContext
            DeviceRepository.init(applicationContext)
            if (!isInit && uuidString != null) {
                deviceNameServiceUuid = UUID.fromString(
                    uuidString
                )

                bluetoothManager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothManager?.let {
                    if (it.adapter == null || !it.adapter.isEnabled()) return // bail if bluetooth isn't on
                    bluetoothLeScanner = it.adapter.bluetoothLeScanner
                    bluetoothGattServer =
                        it.openGattServer(
                            context,
                            object : BluetoothGattServerCallback() {}
                        )
                    bluetoothLeAdvertiser = it.adapter.bluetoothLeAdvertiser
                }

                isInit = true

                // If we weren't paused we're started and in the background...
                if (!isPaused) isStarted.postValue(true) else isStarted.postValue(false)
            }
        }


        NotificationUtils.init(applicationContext)

    }




    fun getAlarmManager(applicationContext: Context): AlarmManager {
        return applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

}