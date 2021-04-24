package com.thesis.distanceguard.presentation.team

import ai.kun.opentracesdk_fat.BLETrace
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.camera.CameraActivity
import com.thesis.distanceguard.util.BarcodeEncoder
import kotlinx.android.synthetic.main.fragment_team.*
import timber.log.Timber
import java.util.*

/**
 * Created by Viet Hua on 04/10/2021.
 */

class TeamFragment : BaseFragment() {
    private val REQUEST_CAMERA = 4
    private val CAMERA_ACTIVITY = 1

    override fun getResLayoutId(): Int {
        return R.layout.fragment_team
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setQRCode()
        fab_camera.setOnClickListener {
            if (requireContext().applicationContext.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA)
            } else {
                scanBarcode()
            }
        }

        btn_exit_team.setOnClickListener {
            BLETrace.leaveTeam()
            setQRCode()
            setTeamCount()

        }
    }

    override fun onResume() {
        super.onResume()
        setTeamCount()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                if (!grantResults.isEmpty()) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Timber.d("...Request for camera access denied.")
                    } else {
                        Timber.d( "...Request for camera access granted.")
                        scanBarcode()
                    }
                } else {
                    // for some lame reason Android gives you a result with nothing in it before
                    // there is a real result?
                    Timber.d( "Prompting for camera response...")
                }
            }
            else -> {
                Timber.d("Unknown request code: $requestCode")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_ACTIVITY) {
            if (data == null) {
                Timber.d("Data returned from intent was null.")
            }
            data?.let {
                val uuidString = it.getStringExtra("UUID")
                if (uuidString == null) {
                    Timber.d("Data returned from intent had a UUID that was null.")
                }
                uuidString?.let {
                    try {
                        val uuid = UUID.fromString(uuidString)
                        var newSet = BLETrace.teamUuids!!.toMutableSet()
                        newSet.add(uuidString)
                        BLETrace.teamUuids = newSet
                    } catch (e: IllegalArgumentException) {
                        Timber.d("Data returned was not a UUID.")
                    }
                }
            }
        }
    }

    private fun scanBarcode() {
        startActivityForResult(Intent(activity, CameraActivity::class.java), CAMERA_ACTIVITY)
    }

    private fun setTeamCount() {
        val text = getString(R.string.your_team_has_0_people)
        val count = BLETrace.teamUuids?.let { it.size } ?: 0
        tv_team_count?.let { it.text = text.replace("0", count.toString(), true) }
    }


    /**
     * set the QR code to be the current UUID
     *
     */
    private fun setQRCode() {
        // Show the QR Code...
        val barCodeImageView = view?.findViewById<ImageView>(R.id.barcodeImage)
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix =
                multiFormatWriter.encode("Just a test barcode", BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            barCodeImageView?.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }
}