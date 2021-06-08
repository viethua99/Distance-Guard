package com.thesis.distanceguard.presentation.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.repository.DeviceRepository
import com.thesis.distanceguard.ble_module.state.BLEStatus
import com.thesis.distanceguard.ble_module.util.BluetoothUtils
import com.thesis.distanceguard.ble_module.util.Constants
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.service.DistanceGuardService
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_scanner.*
import kotlinx.android.synthetic.main.fragment_team.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    private lateinit var item: MenuItem
    override fun getResLayoutId(): Int {
        return R.layout.fragment_scanner
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setHasOptionsMenu(true)
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
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
       activity?.menuInflater!!.inflate(R.menu.menu_scanner, menu)
         item = menu?.findItem(R.id.item_background_scan)
        item?.let {
            val mainActivity = activity as MainActivity
            if (mainActivity.isServiceRunning(DistanceGuardService::class.java)) {
                item.icon = resources.getDrawable(R.drawable.ic_background_scan_off)
            } else {
                item.icon = resources.getDrawable(R.drawable.ic_background_scan_on)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_background_scan -> {
             val mainActivity = activity as MainActivity
                if (isLocationEnabled() && BluetoothAdapter.getDefaultAdapter().isEnabled) {
                    mainActivity.triggerDistanceGuardService()
                    if (mainActivity.distanceGuardService != null) {

                        if (mainActivity.isServiceRunning(mainActivity.distanceGuardService!!::class.java)) {
                            showToastMessage("Background scan started")
                            item.icon = resources.getDrawable(R.drawable.ic_background_scan_off)
                            item.title = "Stop Background"
                        } else {
                            showToastMessage("Background scan stopped")
                            item.icon = resources.getDrawable(R.drawable.ic_background_scan_on)
                            item.title = "Start Background"
                        }
                    }
                } else {
                    showToastMessage("Please check bluetooth and location")
                }

            }
        }
        return true
    }


    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        scannerViewModel =
            ViewModelProvider(this, viewModelFactory).get(ScannerViewModel::class.java)

        scannerViewModel.bleStatus.observe(viewLifecycleOwner, Observer {
            it.let {
                when(it) {
                    BLEStatus.BLUETOOTH_STATE_CHANGED -> {
                        showBluetoothEnableView()
                        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {
                            item.icon = resources.getDrawable(R.drawable.ic_background_scan_on)
                            val activity = activity as MainActivity
                            activity.stopService()
                            BLEController.isPaused = true
                        }
                    }
                    BLEStatus.LOCATION_STATE_CHANGED -> {
                        showLocationEnableView()
                        if (!isLocationEnabled()) {
                            item.icon = resources.getDrawable(R.drawable.ic_background_scan_on)
                            val activity = activity as MainActivity
                            activity.stopService()
                            BLEController.isPaused = true
                        }
                    }
                }
            }
        })
        scannerViewModel.isBLEStarted.observe(viewLifecycleOwner, Observer { isStarted ->
            Timber.d("isStarted = $isStarted")
            isStarted?.let {
                setupVisibilities(it)
            }
        })

        scannerViewModel.scannedDevice.observe(this, scannedDeviceObserver)
    }

    private fun setupVisibilities(isStarted: Boolean) {
        if (isStarted) {
            // Update the devices
            GlobalScope.launch { DeviceRepository.updateCurrentDevices() }
            btn_scanning.text = getString(R.string.fragment_scanner_stop_scanning)
            btn_scanning.setBackgroundColor(Color.parseColor("#ff5050"))
            ll_press_start_to_scan.visibility = View.GONE
            cl_safe_ripple.visibility = View.VISIBLE
            bg_safe_ripple.startRippleAnimation()
        } else {
            btn_scanning.text = getString(R.string.fragment_scanner_start_scanning)
            btn_scanning.setBackgroundColor(Color.parseColor("#007bff"))
            ll_press_start_to_scan.visibility = View.VISIBLE
            cl_scanning_list.visibility = View.GONE
            cl_safe_ripple.visibility = View.GONE
            bg_safe_ripple.stopRippleAnimation()

        }
    }

    private fun setupViews() {
        showBluetoothEnableView()
        showLocationEnableView()
        BLEController.isStarted.value?.let {
            setupVisibilities(it)
        }
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

    private fun showBluetoothEnableView() {
        Timber.d("showBluetoothEnableView")
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled) {

            bluetooth_enable.visibility = View.VISIBLE
            bluetooth_enable_btn.setOnClickListener {
                BluetoothAdapter.getDefaultAdapter().enable()
            }
        } else {
            bluetooth_enable.visibility = View.GONE
        }
    }

    private fun showLocationEnableView() {
        Timber.d("showLocationEnableView")
        if (!isLocationEnabled()) {
            location_enable.visibility = View.VISIBLE
            location_enable_btn.setOnClickListener {
                location_enable.visibility = View.GONE
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                if (Build.VERSION.SDK_INT >= 23)
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        PERMISSIONS_REQUEST_CODE
                    )
            }
        } else {
            location_enable.visibility = View.GONE
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

    private fun showVisibilities(devices: List<Device>) {
        if (devices.isEmpty()) {
            cl_safe_ripple.visibility = View.VISIBLE
            bg_safe_ripple.startRippleAnimation()
            cl_scanning_list.visibility = View.GONE
            bg_danger_ripple.stopRippleAnimation()

        } else {
            val signalStrengthList = mutableListOf<Int>()
            val memberList = mutableListOf<Device>()
            for (device in devices) {
                val signal = BluetoothUtils.calculateSignal(device.rssi, device.txPower)
                signalStrengthList.add(signal)

                if (device.isTeamMember) {
                    memberList.add(device)
                }
            }

            if (memberList.size == devices.size) {
                tv_warning_title.text = "You are safe"
                tv_warning_title.setTextColor(
                    ContextCompat.getColor(
                        context!!,
                        R.color.primary_green
                    )
                )

                bg_warning_ripple.visibility = View.GONE
                bg_warning_ripple.stopRippleAnimation()

                bg_danger_ripple.visibility = View.GONE
                bg_danger_ripple.stopRippleAnimation()

                bg_safe2_ripple.visibility = View.VISIBLE
                bg_safe2_ripple.startRippleAnimation()
            } else {
                when {
                    signalStrengthList.max()!! <= Constants.SIGNAL_DISTANCE_OK -> {
                        tv_warning_title.text = "You are safe"
                        tv_warning_title.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.primary_green
                            )
                        )

                        bg_warning_ripple.visibility = View.GONE
                        bg_warning_ripple.stopRippleAnimation()

                        bg_danger_ripple.visibility = View.GONE
                        bg_danger_ripple.stopRippleAnimation()

                        bg_safe2_ripple.visibility = View.VISIBLE
                        bg_safe2_ripple.startRippleAnimation()

                    }

                    signalStrengthList.max()!! <= Constants.SIGNAL_DISTANCE_STRONG_WARN -> {
                        tv_warning_title.text = "Warning"
                        tv_warning_title.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.primary_orange
                            )
                        )

                        bg_safe2_ripple.visibility = View.GONE
                        bg_safe2_ripple.stopRippleAnimation()


                        bg_danger_ripple.visibility = View.GONE
                        bg_danger_ripple.stopRippleAnimation()

                        bg_warning_ripple.visibility = View.VISIBLE
                        bg_warning_ripple.startRippleAnimation()
                    }
                    else -> {
                        tv_warning_title.text = "Danger!!!"
                        tv_warning_title.setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.primary_red
                            )
                        )

                        bg_safe2_ripple.visibility = View.GONE
                        bg_safe2_ripple.stopRippleAnimation()

                        bg_warning_ripple.visibility = View.GONE
                        bg_warning_ripple.stopRippleAnimation()

                        bg_danger_ripple.visibility = View.VISIBLE
                        bg_danger_ripple.startRippleAnimation()
                    }

                }
            }

            cl_safe_ripple.visibility = View.GONE
            bg_safe_ripple.stopRippleAnimation()
            cl_scanning_list.visibility = View.VISIBLE

            val text = getString(R.string.people_around_you)
            val count = devices.size
            tv_waring_message?.let { it.text = text.replace("0", count.toString(), true) }

        }
    }

    private val onScanButtonClickListener = View.OnClickListener {
        Timber.d("onScanButtonClickListener: clicked")
        checkPermissions()
    }

    private val scannedDeviceObserver = Observer<List<Device>> { devices ->
        devices?.let { deviceList ->
            showVisibilities(deviceList)
            scannerRecyclerViewAdapter.setDataList(deviceList)

        } ?: kotlin.run {
            cl_safe_ripple.visibility = View.VISIBLE
            bg_safe_ripple.startRippleAnimation()
        }
    }

}