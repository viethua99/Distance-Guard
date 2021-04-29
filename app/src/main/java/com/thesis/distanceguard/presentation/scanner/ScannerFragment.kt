package com.thesis.distanceguard.presentation.scanner

import ai.kun.opentracesdk_fat.dao.Device
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_scanner.*
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class ScannerFragment : BaseFragment() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 12

    }

    private lateinit var scannerViewModel: ScannerViewModel
    private lateinit var scannerRecyclerViewAdapter: ScannerRecyclerViewAdapter

    override fun getResLayoutId(): Int {
        return R.layout.fragment_scanner
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setupViewModel()
        setupViews()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty())) {
                    grantResults.forEach { result ->
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            return
                        }
                        if (!BluetoothAdapter.getDefaultAdapter().isEnabled || !isLocationEnabled()) {
                            showToastMessage("Please enable bluetooth and location")
                        } else {
                            scannerViewModel.triggerBLEScan()
                        }
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        scannerViewModel = ViewModelProvider(this, viewModelFactory).get(ScannerViewModel::class.java)

        scannerViewModel.isBLEStarted.observe(viewLifecycleOwner, Observer { isStarted ->
            isStarted?.let {
                if (it) {
                    btn_scanning.text = getString(R.string.fragment_scanner_stop_scanning)
                    btn_scanning.setBackgroundColor(Color.parseColor("#F44336"))
                    cl_safe_ripple.visibility = View.VISIBLE
                    looking_for_devices_background.visibility = View.GONE
                    bg_safe_ripple.startRippleAnimation()
                    bg_danger_ripple.startRippleAnimation()
                } else {
                    btn_scanning.text = getString(R.string.fragment_scanner_start_scanning)
                    btn_scanning.setBackgroundColor(Color.parseColor("#0288D1"))
                    cl_safe_ripple.visibility = View.GONE
                    looking_for_devices_background.visibility = View.VISIBLE
                    bg_safe_ripple.stopRippleAnimation()
                }
            }
        })

        scannerViewModel.scannedDevice.observe(this, scannedDeviceObserver)

    }

    private fun setupViews() {
        btn_scanning.setOnClickListener(onScanButtonClickListener)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(view!!.context)
        scannerRecyclerViewAdapter = ScannerRecyclerViewAdapter(view!!.context)

        rv_scanner.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = scannerRecyclerViewAdapter
        }
    }


    private fun checkPermissions() {
        Timber.d("checkPermissions")
        val reqPermissions = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            reqPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (reqPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity!!,
                reqPermissions.toTypedArray(),
                PERMISSIONS_REQUEST_CODE
            )
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled || !isLocationEnabled()) {
                showToastMessage("Please enable bluetooth and location")
            } else {
                scannerViewModel.triggerBLEScan()
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        return locationManager?.let {
            it.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || it.isProviderEnabled(
                LocationManager.GPS_PROVIDER
            )
        } ?: false
    }

    private val onScanButtonClickListener = View.OnClickListener {
        Timber.d("onScanButtonClickListener: clicked")
        checkPermissions()
    }

    private val scannedDeviceObserver = Observer<List<Device>> {devices ->
        devices?.let {
            if (devices.isEmpty()) {
                looking_for_devices_background.visibility = View.VISIBLE
                rv_scanner.visibility = View.GONE
            } else {
                looking_for_devices_background.visibility = View.GONE
                rv_scanner.visibility = View.VISIBLE
                scannerRecyclerViewAdapter.setDataList(it)

            }
        } ?: kotlin.run {
            looking_for_devices_background.visibility = View.VISIBLE
            rv_scanner.visibility = View.GONE
        }
    }

}