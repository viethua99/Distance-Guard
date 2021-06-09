package com.thesis.distanceguard.presentation.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.repository.DeviceRepository
import com.thesis.distanceguard.ble_module.state.BLEStatus
import com.thesis.distanceguard.ble_module.state.BluetoothStateReceiver
import com.thesis.distanceguard.ble_module.state.LocationStateReceiver
import timber.log.Timber
import javax.inject.Inject

class ScannerViewModel @Inject constructor(
    bluetoothStateReceiver: BluetoothStateReceiver,
    locationStateReceiver: LocationStateReceiver
) : ViewModel(),  BluetoothStateReceiver.BluetoothStateListener,
    LocationStateReceiver.LocationStateListener {
    val isBLEStarted: MutableLiveData<Boolean> = BLEController.isStarted
    val scannedDevice: MutableLiveData<List<Device>> = DeviceRepository.currentDevices
    val bleStatus = MutableLiveData<BLEStatus>()

    init {
        bluetoothStateReceiver.addListener(this)
        locationStateReceiver.addListener(this)
    }

   fun triggerBLEScan(){
       isBLEStarted.value?.let { isStarted ->
           Timber.d("triggerBLEScan: $isStarted")
           BLEController.isPaused = isStarted
       }
   }

    override fun onBluetoothStateChanged(enabled: Boolean) {
        Timber.d("onBluetoothStateChanged: $enabled")
        bleStatus.value = BLEStatus.BLUETOOTH_STATE_CHANGED
    }

    override fun onLocationStateChanged() {
        Timber.d("onLocationStateChanged")
        bleStatus.value =  BLEStatus.LOCATION_STATE_CHANGED

    }
}