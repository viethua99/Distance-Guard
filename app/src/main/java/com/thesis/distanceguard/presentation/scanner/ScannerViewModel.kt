package com.thesis.distanceguard.presentation.scanner

import ai.kun.opentracesdk_fat.BLETrace
import ai.kun.opentracesdk_fat.DeviceRepository
import ai.kun.opentracesdk_fat.dao.Device
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ScannerViewModel @Inject constructor() : ViewModel(){
    val isBLEStarted: MutableLiveData<Boolean> = BLETrace.isStarted
    val scannedDevice: MutableLiveData<List<Device>> = DeviceRepository.currentDevices

   fun triggerBLEScan(){
       isBLEStarted.value?.let { isStarted ->
           BLETrace.isPaused = isStarted
       }
   }
}