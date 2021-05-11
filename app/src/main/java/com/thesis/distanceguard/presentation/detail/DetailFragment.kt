package com.thesis.distanceguard.presentation.detail

import android.graphics.Color

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalCountryResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.*
import timber.log.Timber

class DetailFragment() : BaseFragment() {
    constructor(itemCountry: CountryResponse) : this() {
        this.itemCountry = itemCountry
    }

    companion object {
        const val TAG = "DetailFragment"
        private const val animationDuration = 1000L
    }

    private lateinit var itemCountry: CountryResponse
    private lateinit var detailViewModel: DetailViewModel
    
    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)
        setupViewModel()
        setupLineChart()
        fetchCountry()
    }

    private fun setupViewModel() {
        AndroidSupportInjection.inject(this)
        detailViewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
        detailViewModel.errorMessage.observe(this, Observer {
            hideDialog()
            showToastMessage(it)
        })
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

    private fun setupLineChart() {
        chart_cases.gradientFillColors =
            intArrayOf(
                Color.parseColor("#99caff"),
                Color.TRANSPARENT
            )
        chart_cases.animation.duration = animationDuration


        chart_recovered.gradientFillColors =
            intArrayOf(
                Color.parseColor("#a6ecbb"),
                Color.TRANSPARENT
            )
        chart_recovered.animation.duration = animationDuration


        chart_death.gradientFillColors =
            intArrayOf(
                Color.parseColor("#ffb9b9"),
                Color.TRANSPARENT
            )
        chart_death.animation.duration = animationDuration
    }

    private fun fetchCountry() {
        showProgressDialog("Fetching data")
        detailViewModel.fetchCountry(itemCountry.countryInfo.id.toString())
            .observe(this, countryObserver)
    }

    private val countryObserver = Observer<HistoricalCountryResponse> {
        hideDialog()
        chart_cases.animate(
            AppUtil.convertPairLongToPairFloat(
                it.timeline.cases.toList().sortedBy { value -> value.second })
        )

        chart_recovered.animate(
            AppUtil.convertPairLongToPairFloat(
                it.timeline.recovered.toList().sortedBy { value -> value.second })
        )

        chart_death.animate(
            AppUtil.convertPairLongToPairFloat(
                it.timeline.deaths.toList().sortedBy { value -> value.second })
        )

        //update text view
        val length = it.timeline.cases.toList().size - 1
        tv_comfirmed_count.text = AppUtil.toNumberWithCommas(
            it.timeline.cases.toList().sortedBy { value -> value.second }[length].second
        )
        tv_recovered_count.text = AppUtil.toNumberWithCommas(
            it.timeline.recovered.toList().sortedBy { value -> value.second }[length].second
        )
        tv_death_count.text = AppUtil.toNumberWithCommas(
            it.timeline.deaths.toList().sortedBy { value -> value.second }[length].second
        )

        updateOtherInformation()
    }

    private fun updateMainInformation() {
//        tv_total_cases_count.text = AppUtil.toNumberWithCommas(itemCountry.cases.toLong())
//        tv_recover_count.text = AppUtil.toNumberWithCommas(itemCountry.recovered.toLong())
//        tv_active_count.text = AppUtil.toNumberWithCommas(itemCountry.active.toLong())
//        tv_deaths_count.text = AppUtil.toNumberWithCommas(itemCountry.deaths.toLong())
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