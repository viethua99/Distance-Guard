package com.thesis.distanceguard.presentation.camera

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View

import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.thesis.distanceguard.R
import android.hardware.Camera
import androidx.lifecycle.Observer
import com.thesis.distanceguard.barcode_detection.BarcodeProcessor
import com.thesis.distanceguard.camera_module.CameraSource
import com.thesis.distanceguard.camera_module.CameraSourcePreview
import com.thesis.distanceguard.camera_module.GraphicOverlay
import com.thesis.distanceguard.presentation.base.BaseActivity
import dagger.android.AndroidInjection
import timber.log.Timber
import java.io.IOException

/**
 * Created by Viet Hua on 04/19/2021.
 */

class CameraActivity : BaseActivity(), View.OnClickListener {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var cameraViewModel: CameraViewModel? = null
    private var currentCameraViewState: CameraViewModel.WorkflowState? = null
    override fun getResLayoutId(): Int {
        return R.layout.activity_camera
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            setOnClickListener(this@CameraActivity)
            cameraSource = CameraSource(this)
        }

        promptChip = findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        findViewById<View>(R.id.close_button).setOnClickListener(this)
        flashButton = findViewById<View>(R.id.flash_button).apply {
            setOnClickListener(this@CameraActivity)
        }

        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()

        cameraViewModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        currentCameraViewState = CameraViewModel.WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, cameraViewModel!!))
        cameraViewModel?.setWorkflowState(CameraViewModel.WorkflowState.DETECTING)
    }

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onPause() {
        super.onPause()
        currentCameraViewState = CameraViewModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    private fun startCameraPreview() {
        val workflowModel = this.cameraViewModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Timber.e("Failed to start camera preview!")
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.cameraViewModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpWorkflowModel() {
        AndroidInjection.inject(this)
        cameraViewModel = ViewModelProvider(this,viewModelFactory).get(CameraViewModel::class.java)


        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        cameraViewModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || (currentCameraViewState == workflowState)) {
                return@Observer
            }

            currentCameraViewState = workflowState
            Timber.d("Current workflow state: ${currentCameraViewState!!.name}")

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                CameraViewModel.WorkflowState.DETECTING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }
                CameraViewModel.WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }
                CameraViewModel.WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }
                CameraViewModel.WorkflowState.DETECTED, CameraViewModel.WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }
                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        cameraViewModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
               showToastMessage(barcode.displayValue!!)
            }

        })
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_button -> onBackPressed()
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }
        }
    }

}