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


/**
 * Most of Android's underling BLE implementation is not thread safe and yet we need to call this
 * stuff all over the place in the application from different threads and even different contexts.
 * This object represents all the underling BLE functionality for device detection.  It's the primary
 * interface for this library. It's quite complicated and does a bunch of work for you including
 * dealing with the UUID and if you want, dealing with the data (by putting it in a database).
 *
 * Thread safety is implemented by synchronizing on this object.
 */
object BLEController {
    private val mBLEAdvertiser : BLEAdvertiser =
        BLEAdvertiser()
    private val mBLEScanner : BLEScanner =
        BLEScanner()
    private val TAG = "BLEController"

    private var isInit = false
    private lateinit var context : Context
    var bluetoothGattServer : BluetoothGattServer? = null
    var bluetoothManager : BluetoothManager? = null
    var bluetoothLeScanner : BluetoothLeScanner? = null
    var bluetoothLeAdvertiser : BluetoothLeAdvertiser? = null

    var isBackground : Boolean = true

    // Live data you can use to see if things have been started
    val isStarted: MutableLiveData<Boolean> = MutableLiveData(false)

    // A thread safe way to check if things are paused
    var isPaused : Boolean
        get() {
            synchronized(this) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_FILE_NAME, Context.MODE_PRIVATE
                )
                return sharedPrefs.getBoolean(PREF_IS_PAUSED, true)
            }
        }
        set(value) {
            synchronized(this) {
                val sharedPrefs = context.getSharedPreferences(
                    PREF_FILE_NAME, Context.MODE_PRIVATE
                )
                val editor: SharedPreferences.Editor = sharedPrefs.edit()
                editor.putBoolean(PREF_IS_PAUSED, value)
                editor.apply()
                if (value) {
                    stop()
                } else {
                    start(isBackground)
                }
            }
        }

    // A thread safe way to set the UUID used by the device
    var uuidString : String?
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
                    stop()
                }
            }
        }

    // A thread safe way to set a list of UUID's to mark as team members when they are detected
    var teamUuids : Set<String>?
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

    /**
     * leaving the current team will automatically give you a new UUID and forget all the UUIDs that
     * were stored as team members.
     *
     */
    fun leaveTeam() {
        synchronized( this) {
            teamUuids = null
            uuidString =
                getNewUniqueId()
            isStarted.value?.let {
                if (it) {
                    stop()
                    start(isBackground)
                }
            }
        }
    }

    /**
     * Check to see if a UUID is part of the UUIDs currently saved as team members
     *
     * @param scannnedUuid The UUID to check
     * @return True if the UUID belongs to a list of UUIDs currently stored as team members
     */
    fun isTeamMember(scannnedUuid: String): Boolean {
        synchronized(this) {
            return teamUuids?.let {
                it.toTypedArray().contains(scannnedUuid)
            } ?: false
        }
    }


    /**
     * create a new UUID
     *
     * @return a UUID as a string
     */
    fun getNewUniqueId() : String {
        val stringChars = (('0'..'9') + ('a'..'f')).toList().toTypedArray()
        val id = "0${((1..11).map { stringChars.random() }.joinToString(""))}"
        return Constants.SERVICE_STRING.replaceAfterLast('-', id)
    }

    lateinit var deviceNameServiceUuid: UUID

    /**
     * Start the BLE scanning and detection
     *
     * @param startingBackground Set to true to use the background intervals.  Right now this doesn't
     * make any difference.
     */
    fun start(startingBackground: Boolean) {
        Timber.d("start")
        synchronized(this) {
            if (isStarted.value!!) stop()
            if (startingBackground) startBackground() else startForeground()
        }
    }

    /**
     * Stop the BLE scanning and detection
     *
     */
    fun stop() {
        synchronized(this) {
            if (isBackground) stopBackground() else stopForeground()
        }
    }

    /**
     * The following methods deal with the problem that your intent that you use to stop
     * an alarm manager has to be identical to the intent that you used to stop it.  So
     * for that to be true you have to cancel the alarm with the correct argument for
     * background vs foreground, and thus a bunch of code...
     */

    /**
     * Start scanning and broadcasting using the values for the background mode
     *
     */
    private fun startBackground() {
        if (isEnabled() && !isPaused) {
            Log.i(TAG, "startBackground")
            isBackground = true
            isStarted.postValue(true)
            mBLEAdvertiser.enable(Constants.REBROADCAST_PERIOD,
                context
            )
            mBLEScanner.enable(Constants.BACKGROUND_TRACE_INTERVAL,
                context
            )
        } else {
            Log.i(TAG, "startBackground not possible");
            isStarted.postValue(false)
        }
    }

    /**
     * Start scanning and broadcasting using the values for the forground mode
     *
     */
    private fun startForeground() {
        if (isEnabled() && !isPaused) {
            Log.i(TAG, "startForeground")
            isBackground = false
            isStarted.postValue(true)
            mBLEAdvertiser.enable(Constants.REBROADCAST_PERIOD,
                context
            )
            mBLEScanner.enable(Constants.FOREGROUND_TRACE_INTERVAL,
                context
            )
        } else {
            isStarted.postValue(false)
        }
    }

    /**
     * Stop scanning and broadcasting when things were started in background mode
     *
     */
    private fun stopBackground() {
        if (isEnabled()) {
            mBLEAdvertiser.disable(Constants.REBROADCAST_PERIOD,
                context
            )
            mBLEScanner.disable(Constants.BACKGROUND_TRACE_INTERVAL,
                context
            )
        }
        isStarted.postValue(false)
    }

    /**
     * Stop scanning and broadcasting when things were started in forground mode
     *
     */
    private fun stopForeground() {
        if (isEnabled()) {
            mBLEAdvertiser.disable(Constants.REBROADCAST_PERIOD,
                context
            )
            mBLEScanner.disable(Constants.FOREGROUND_TRACE_INTERVAL,
                context
            )
        }
        isStarted.postValue(false)
    }


    /**
     * Check to see if BLE Trace is enabled
     *
     * @return True if the BLE Trace is ready to be used
     */
    fun isEnabled() : Boolean {
        bluetoothManager?.let {
            if (uuidString == null || it.adapter == null || !it.adapter.isEnabled()) return false
        }

        if (!isInit) init(context) // If bluetooth was off we need to complete the init

        return isInit  // && isLocationEnabled() Location doesn't need to be on
    }

    /**
     * Initialize BLE Trace.  This needs to be called as early as possible in the application.
     *
     * @param applicationContext The application context to use for init

     */
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
                            object : BluetoothGattServerCallback(){}
                        )
                    bluetoothLeAdvertiser = it.adapter.bluetoothLeAdvertiser
                }

                isInit = true

                // If we weren't paused we're started and in the background...
                if (!isPaused) isStarted.postValue(true) else isStarted.postValue(false)
                isBackground = true
            }
        }


            NotificationUtils.init(applicationContext)

    }

    /**
     * Get the AlarmManager
     *
     * @param applicationContext The context to use
     * @return An AlarmManager instance associated with the context
     */
    fun getAlarmManager(applicationContext: Context): AlarmManager {
        return applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * Check to see if location manager can be used.  Not currently needed.
     *
     * @return True if we have the user's permission to use location information.
     */
    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }
}