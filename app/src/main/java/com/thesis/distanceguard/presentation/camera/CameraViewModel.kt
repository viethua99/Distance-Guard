package com.thesis.distanceguard.presentation.camera

import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.thesis.distanceguard.camera_module.DetectedObject
import com.thesis.distanceguard.myapp.MyApplication
import java.util.HashSet
import javax.inject.Inject

class CameraViewModel @Inject constructor(myApplication: MyApplication) : AndroidViewModel(myApplication) {

    val workflowState = MutableLiveData<WorkflowState>()
    val detectedBarcode = MutableLiveData<FirebaseVisionBarcode>()

    private val objectIdsToSearch = HashSet<Int>()

    var isCameraLive = false
        private set

    private var confirmedObject: DetectedObject? = null


    enum class WorkflowState {
        NOT_STARTED,
        DETECTING,
        DETECTED,
        CONFIRMING,
        CONFIRMED,
        SEARCHING,
        SEARCHED
    }

    @MainThread
    fun setWorkflowState(workflowState: WorkflowState) {
        if (workflowState != WorkflowState.CONFIRMED &&
                workflowState != WorkflowState.SEARCHING &&
                workflowState != WorkflowState.SEARCHED
        ) {
            confirmedObject = null
        }
        this.workflowState.value = workflowState
    }


    fun markCameraLive() {
        isCameraLive = true
        objectIdsToSearch.clear()
    }

    fun markCameraFrozen() {
        isCameraLive = false
    }
}
