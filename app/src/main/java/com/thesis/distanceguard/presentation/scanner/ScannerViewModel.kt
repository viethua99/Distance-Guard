package com.thesis.distanceguard.presentation.scanner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.repository.DeviceRepository
import javax.inject.Inject

class ScannerViewModel @Inject constructor() : ViewModel(){
    val isBLEStarted: MutableLiveData<Boolean> = BLEController.isStarted
    val scannedDevice: MutableLiveData<List<Device>> = DeviceRepository.currentDevices

   fun triggerBLEScan(){
       isBLEStarted.value?.let { isStarted ->
           BLEController.isPaused = isStarted
       }
   }
}