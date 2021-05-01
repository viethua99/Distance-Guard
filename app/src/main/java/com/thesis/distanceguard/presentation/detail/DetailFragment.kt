package com.thesis.distanceguard.presentation.detail

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalWorldwideResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.*
import timber.log.Timber

class DetailFragment() : BaseFragment() {
    constructor(item: CountryResponse) : this() {
        itemCountry = item
    }

    companion object {
        const val TAG = "DetailFragment"
        lateinit var itemCountry: CountryResponse

    }


    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)

        updateMainInformation()
        updateOtherInformation()
    }


    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        setupToolbarTitle(itemCountry.country)
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        setupToolbarTitle("Countries")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun updateMainInformation() {
        tv_total_cases_count.text = AppUtil.toNumberWithCommas(itemCountry.cases.toLong())
        tv_recover_count.text = AppUtil.toNumberWithCommas(itemCountry.recovered.toLong())
        tv_active_count.text = AppUtil.toNumberWithCommas(itemCountry.active.toLong())
        tv_deaths_count.text = AppUtil.toNumberWithCommas(itemCountry.deaths.toLong())
    }

    private fun updateOtherInformation() {
        tv_test_count.text = AppUtil.toNumberWithCommas(itemCountry.tests.toLong())
        tv_population_count.text = AppUtil.toNumberWithCommas(itemCountry.population.toLong())
        tv_one_case_per_people_count.text =
            AppUtil.toNumberWithCommas(itemCountry.oneCasePerPeople.toLong())
        tv_one_test_per_people_count.text =
            AppUtil.toNumberWithCommas(itemCountry.oneTestPerPeople.toLong())
        tv_one_death_per_people_count.text =
            AppUtil.toNumberWithCommas(itemCountry.oneDeathPerPeople.toLong())
        tv_case_per_one_million_count.text =
            AppUtil.toNumberWithCommas(itemCountry.casesPerOneMillion.toLong())
        tv_death_per_one_million_count.text =
            AppUtil.toNumberWithCommas(itemCountry.deathsPerOneMillion.toLong())
        tv_test_per_one_million_count.text =
            AppUtil.toNumberWithCommas(itemCountry.testsPerOneMillion.toLong())
        tv_active_per_one_million_count.text =
            AppUtil.toNumberWithCommas(itemCountry.activePerOneMillion.toLong())
    }


}