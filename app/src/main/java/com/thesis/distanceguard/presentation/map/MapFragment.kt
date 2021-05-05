package com.thesis.distanceguard.presentation.map

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlin.math.pow

class MapFragment : BaseFragment(), OnMapReadyCallback {
    private val markers = mutableListOf<Marker>()
    private var googleMap: GoogleMap? = null
    private var pulseCircle: Circle? = null

    private val caseType by lazy {
        arguments?.getInt(TYPE) ?: CaseType.FULL
    }

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapRecyclerViewAdapter: MapRecyclerViewAdapter

    companion object {
        const val TAG = "MapFragment"
        private const val LAT_DEFAULT = 30.360227
        private const val LON_DEFAULT = 114.8260094

        private val RECOVERED_COLOR = Color.argb(70, 0, 204, 153)
        private val CONFIRMED_COLOR = Color.argb(70, 242, 185, 0)
        private val DEATH_COLOR = Color.argb(70, 226, 108, 90)

        private const val TYPE = "type"

        @JvmStatic
        fun newInstance(caseType: Int) = MapFragment().apply {
            arguments = Bundle().apply { putInt(TYPE, caseType) }
        }
    }

    override fun getResLayoutId(): Int {
        return R.layout.fragment_map
    }

    override fun onMyViewCreated(view: View) {
        setupViewModel()
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.GONE

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fr) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fetchCountry()
        setupRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.VISIBLE

    }

    private fun setupViewModel() {
        AndroidSupportInjection.inject(this)
        mapViewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mapRecyclerViewAdapter = MapRecyclerViewAdapter(context!!)
        recycler_view.apply {
            layoutManager = linearLayoutManager
            adapter = mapRecyclerViewAdapter
        }
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
//    private fun selectItem(data: LocationItem) {
//        googleMap?.let {
//            moveCamera(LatLng(data.lat, data.long))
//            startPulseAnimation(LatLng(data.lat, data.long))
//        }
//    }
//
//    private val valueAnimator by lazy {
//        ValueAnimator.ofFloat(
//            0f,
//            calculatePulseRadius(googleMap?.cameraPosition?.zoom ?: 4f).apply { }
//        ).apply {
//            startDelay = 100
//            duration = 800
//            interpolator = AccelerateDecelerateInterpolator()
//        }
//    }

    private fun fetchCountry() {
        mapViewModel.fetchCountryList().observe(this, countryListObserver)
    }

    private val countryListObserver = Observer<ArrayList<CountryResponse>> {
        it?.let {
            updateMarkers(it)
            for(i in it.indices){
                mapRecyclerViewAdapter.addData(it[i])
            }
        }
    }

    private fun updateMarkers(data: ArrayList<CountryResponse>) {
        googleMap?.clear()
        markers.clear()
        data.filterIsInstance<CountryResponse>().forEach {
            val marker = googleMap?.addMarker(
                MarkerOptions().position(LatLng(it.countryInfo.lat, it.countryInfo.long))
                    .anchor(0.5f, 0.5f)
                    .title(it.country)
                    .icon(
                        BitmapDescriptorFactory.fromResource(
                            when (caseType) {
                                CaseType.DEATHS -> R.drawable.img_deaths_marker
                                CaseType.RECOVERED -> R.drawable.img_recovered_marker
                                else -> R.drawable.img_confirmed_marker
                            }
                        )
                    )
            )
            marker?.let { m ->
                markers.add(m)
            }
        }
    }

    private fun calculatePulseRadius(zoomLevel: Float): Float {
        return 2.0.pow(16 - zoomLevel.toDouble()).toFloat() * 160
    }

    private val valueAnimator by lazy {
        ValueAnimator.ofFloat(
            0f,
            calculatePulseRadius(googleMap?.cameraPosition?.zoom ?: 4f).apply { }
        ).apply {
            startDelay = 100
            duration = 800
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    private fun startPulseAnimation(latLng: LatLng) {
        valueAnimator?.apply {
            removeAllUpdateListeners()
            removeAllListeners()
            end()
        }

        pulseCircle?.remove()
        pulseCircle = googleMap?.addCircle(
            CircleOptions().center(
                latLng
            ).radius(0.0).strokeWidth(0f)
        )

        valueAnimator.addUpdateListener {
            pulseCircle?.fillColor = when (caseType) {
                CaseType.RECOVERED -> RECOVERED_COLOR
                CaseType.DEATHS -> DEATH_COLOR
                else -> CONFIRMED_COLOR

            }
            pulseCircle?.radius = (valueAnimator.animatedValue as Float).toDouble()
        }

        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                valueAnimator.startDelay = 100
                valueAnimator.start()
            }
        })

        valueAnimator.start()
    }

    object CaseType {
        const val CONFIRMED = 0
        const val DEATHS = 1
        const val RECOVERED = 2
        const val FULL = 3
    }
}


/**
object CaseType {
const val CONFIRMED = 0
const val DEATHS = 1
const val RECOVERED = 2
const val FULL = 3
}
 * */