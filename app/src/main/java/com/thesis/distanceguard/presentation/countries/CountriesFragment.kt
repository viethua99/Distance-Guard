package com.thesis.distanceguard.presentation.countries

import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_countries.*
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class CountriesFragment : BaseFragment() {

    private lateinit var countriesRecyclerViewAdapter: CountriesRecyclerViewAdapter
    private lateinit var countriesViewModel: CountriesViewModel

    override fun getResLayoutId(): Int {
        return R.layout.fragment_countries
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setupViewModel()
        setupRecyclerView()
    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        countriesViewModel = ViewModelProvider(this, viewModelFactory).get(CountriesViewModel::class.java)

        countriesViewModel.fetchCountryList().observe(this,countryListObserver)
    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(view!!.context)
        countriesRecyclerViewAdapter = CountriesRecyclerViewAdapter(view!!.context)

        countriesRecyclerViewAdapter.itemClickListener = object :
            BaseRecyclerViewAdapter.ItemClickListener<CountryResponse> {
            override fun onClick(position: Int, item: CountryResponse) {
                Timber.d("onClick: $item")
                ViewCompat.postOnAnimationDelayed(view!!, // Delay to show ripple effect
                    Runnable {
                        val mainActivity = activity as MainActivity
                        mainActivity.addFragment(DetailFragment(), DetailFragment.TAG,R.id.container_main)
                    }
                    , 50)

            }

            override fun onLongClick(position: Int, item: CountryResponse) {}
        }

        rv_countries.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = countriesRecyclerViewAdapter
        }
    }

    private val countryListObserver = Observer<ArrayList<CountryResponse>> {
        it?.let {
            countriesRecyclerViewAdapter.setDataList(it)
        }
    }
}