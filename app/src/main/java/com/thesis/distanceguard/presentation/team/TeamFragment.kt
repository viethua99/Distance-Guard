package com.thesis.distanceguard.presentation.team

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.util.BarcodeEncoder
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class TeamFragment : BaseFragment() {
    override fun getResLayoutId(): Int {
        return R.layout.fragment_team
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setQRCode()
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