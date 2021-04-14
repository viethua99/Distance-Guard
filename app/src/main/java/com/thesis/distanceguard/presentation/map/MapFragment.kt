package com.thesis.distanceguard.presentation.map

import android.content.res.Resources
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment

class MapFragment : BaseFragment(), OnMapReadyCallback {
    private val markers = mutableListOf<Marker>()
    private var googleMap: GoogleMap? = null
    private var pulseCircle: Circle? = null

    companion object {
        private const val LAT_DEFAULT = 30.360227
        private const val LON_DEFAULT = 114.8260094
    }

    override fun getResLayoutId(): Int {
        return R.layout.fragment_map
    }

    override fun onMyViewCreated(view: View) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fr) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {
        this.googleMap = p0
        try {
            googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_json))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
        moveCamera(LatLng(LAT_DEFAULT, LON_DEFAULT))

    }

    private fun moveCamera(latLng: LatLng) {
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4f))
    }

}