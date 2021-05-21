package com.thesis.distanceguard.presentation.map

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.widget.EditText
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.thesis.distanceguard.R
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.countries.CountriesAdapter
import com.thesis.distanceguard.presentation.countries.MapAdapter
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.room.entities.CountryEntity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.recycler_view
import kotlinx.android.synthetic.main.layout_bottom_sheet.img_clear
import kotlinx.android.synthetic.main.layout_bottom_sheet.edt_search
import timber.log.Timber
import kotlin.math.pow

class MapFragment : BaseFragment(), OnMapReadyCallback {
    private val markers = mutableListOf<Marker>()
    private var googleMap: GoogleMap? = null
    private var pulseCircle: Circle? = null

    private val caseType by lazy {
        arguments?.getInt(TYPE) ?: CaseType.FULL
    }

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapAdapter: MapAdapter

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

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filteredList = CountriesAdapter.filter(
                    mapViewModel.countryList.value!!,
                    p0.toString()
                )
                mapAdapter.replaceAll(filteredList!!)
                recycler_view.scrollToPosition(0)
            }
        })

        img_clear.setOnClickListener {
            edt_search.text.clear()
        }
        fetchCountry()
        setupRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.VISIBLE

    }

    fun EditText.hideKeyboard(): Boolean {
        return (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setupViewModel() {
        AndroidSupportInjection.inject(this)
        mapViewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mapAdapter = MapAdapter(context!!)
        recycler_view.apply {
            layoutManager = linearLayoutManager
            adapter = mapAdapter
        }
        mapAdapter.itemClickListener = object :
            MapAdapter.ItemClickListener<CountryEntity> {
            override fun onClick(position: Int, item: CountryEntity) {
                Timber.d("onClick: $item")
                edt_search.hideKeyboard()
                selectItem(item)
            }

            override fun onLongClick(position: Int, item: CountryEntity) {}
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

    private fun selectItem(data: CountryEntity) {
        googleMap?.let {
            moveCamera(LatLng(data.countryInfoEntity!!.latitude!!, data.countryInfoEntity!!.longitude!!))
            startPulseAnimation(LatLng(data.countryInfoEntity!!.latitude!!, data.countryInfoEntity!!.longitude!!))
        }
    }

    private fun fetchCountry() {
        mapViewModel.fetchCountryList().observe(this, countryListObserver)
    }

    private val countryListObserver = Observer<ArrayList<CountryEntity>> {
        it?.let {
            updateMarkers(it)
            mapAdapter.add(it)
        }
    }

    private fun updateMarkers(data: ArrayList<CountryEntity>) {
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