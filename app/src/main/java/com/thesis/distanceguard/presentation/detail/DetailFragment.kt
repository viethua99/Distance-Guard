package com.thesis.distanceguard.presentation.detail

import android.graphics.Color

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalVietnamResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_detail.*
import timber.log.Timber
import java.util.ArrayList

class DetailFragment() : BaseFragment() {
    constructor(countryID: Int) : this() {
        this.countryID = countryID
    }

    companion object {
        const val TAG = "DetailFragment"
        private const val animationDuration = 1000L


    }

    private var countryID: Int = 0
    private lateinit var detailViewModel: DetailViewModel


    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)
        setupViewModel()
        updateMainInformation()
        updateOtherInformation()
        setupLineChart()
        fetchCountry()
    }

    private fun setupViewModel() {
        AndroidSupportInjection.inject(this)
        detailViewModel = ViewModelProvider(this, viewModelFactory).get(DetailViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        setupToolbarTitle("")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        setupToolbarTitle("Countries")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun setupLineChart() {
        chart_comfirmed.gradientFillColors =
            intArrayOf(
                Color.parseColor("#e6f2ff"),
                Color.TRANSPARENT
            )
        chart_comfirmed.animation.duration = animationDuration


        chart_recovered.gradientFillColors =
            intArrayOf(
                Color.parseColor("#e9faee"),
                Color.TRANSPARENT
            )
        chart_recovered.animation.duration = animationDuration


        chart_death.gradientFillColors =
            intArrayOf(
                Color.parseColor("#ffeff2"),
                Color.TRANSPARENT
            )
        chart_death.animation.duration = animationDuration
    }

    private fun fetchCountry() {
        detailViewModel.fetchCountry(countryID.toString()).observe(this, countryObserver)
    }

    private val countryObserver = Observer<HistoricalVietnamResponse> {
        Timber.d("CK frg " + it.country)

        chart_comfirmed.animate(
            convertPairLongToPairFloat(
                it.timeline.cases.toList().sortedBy { value -> value.second })
        )

        chart_recovered.animate(
            convertPairLongToPairFloat(
                it.timeline.recovered.toList().sortedBy { value -> value.second })
        )

        chart_death.animate(
            convertPairLongToPairFloat(
                it.timeline.deaths.toList().sortedBy { value -> value.second })
        )
    }


    private fun convertPairLongToPairFloat(current: List<Pair<String, Long>>): List<Pair<String, Float>> {
        val newList = ArrayList<Pair<String, Float>>()
        for (i in 1 until current.size) {
            val keySecond = current[i].second.toFloat()
            val newPair = Pair(current[i].first, keySecond)
            newList.add(newPair)
        }
        return newList
    }

    private fun updateMainInformation() {
//        tv_total_cases_count.text = AppUtil.toNumberWithCommas(itemCountry.cases.toLong())
//        tv_recover_count.text = AppUtil.toNumberWithCommas(itemCountry.recovered.toLong())
//        tv_active_count.text = AppUtil.toNumberWithCommas(itemCountry.active.toLong())
//        tv_deaths_count.text = AppUtil.toNumberWithCommas(itemCountry.deaths.toLong())

    }

    private fun updateOtherInformation() {
//        tv_test_count.text = AppUtil.toNumberWithCommas(itemCountry.tests.toLong())
//        tv_population_count.text = AppUtil.toNumberWithCommas(itemCountry.population.toLong())
//        tv_one_case_per_people_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.oneCasePerPeople.toLong())
//        tv_one_test_per_people_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.oneTestPerPeople.toLong())
//        tv_one_death_per_people_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.oneDeathPerPeople.toLong())
//        tv_case_per_one_million_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.casesPerOneMillion.toLong())
//        tv_death_per_one_million_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.deathsPerOneMillion.toLong())
//        tv_test_per_one_million_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.testsPerOneMillion.toLong())
//        tv_active_per_one_million_count.text =
//            AppUtil.toNumberWithCommas(itemCountry.activePerOneMillion.toLong())
    }


}