package com.thesis.distanceguard.presentation.countries

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_countries.*
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class CountriesFragment : BaseFragment() {

    private lateinit var countriesViewModel: CountriesViewModel
    private lateinit var countriesAdapter: CountriesAdapter
    override fun getResLayoutId(): Int {
        return R.layout.fragment_countries
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setupViewModel()
        setupRecyclerView()

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val filteredList = CountriesAdapter.filter(
                    countriesViewModel.countryList.value!!,
                    p0.toString()
                )
                countriesAdapter.replaceAll(filteredList!!)
                rv_countries.scrollToPosition(0)
            }
        })
        img_clear.setOnClickListener {
            edt_search.text.clear()
        }

    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        countriesViewModel =
            ViewModelProvider(this, viewModelFactory).get(CountriesViewModel::class.java)

        countriesViewModel.fetchCountryList().observe(this, countryListObserver)
    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(context)
        countriesAdapter = CountriesAdapter(context!!)
        rv_countries.apply {
            layoutManager = linearLayoutManager
            adapter = countriesAdapter
        }

        countriesAdapter.itemClickListener = object :
            CountriesAdapter.ItemClickListener<CountryResponse> {
            override fun onClick(position: Int, item: CountryResponse) {
                Timber.d("onClick: $item")

                ViewCompat.postOnAnimationDelayed(view!!, // Delay to show ripple effect
                    Runnable {
                        val mainActivity = activity as MainActivity
                        mainActivity.addFragment(
                            DetailFragment(item),
                            DetailFragment.TAG,
                            R.id.container_main
                        )

                    }
                    , 50)

            }

            override fun onLongClick(position: Int, item: CountryResponse) {}
        }
    }

    private val countryListObserver = Observer<ArrayList<CountryResponse>> {
        it?.let {
            countriesAdapter.add(it)
        }
    }
}