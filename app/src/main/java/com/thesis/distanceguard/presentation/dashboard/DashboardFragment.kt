package com.thesis.distanceguard.presentation.dashboard

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.*
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.presentation.countries.CountriesAdapter
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.information.InformationFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber
import java.util.*

class DashboardFragment : BaseFragment() {
    companion object {
        private const val ANIMATION_DURATION = 1000L
    }

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dashboardRecyclerViewAdapter: DashboardRecyclerViewAdapter


    override fun getResLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onMyViewCreated(view: View) {
        setupViewModel()
        setupViews()
        fetchInitData()
    }


    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        dashboardViewModel =
            ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
        dashboardViewModel.fetchCountryList().observe(this, topCountryListObserver)
    }

    private fun setupViews() {
        setupRecyclerView()
        setupLineChart()
        toggleWorldwideSwitch()

        btn_click_here.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.addFragment(
                InformationFragment(),
                InformationFragment.TAG,
                R.id.container_main
            )
        }
    }

    private fun fetchInitData() {
        showProgressDialog("Fetching Data")
        fetchTopCountryList()
        fetchWorldwideData()
        fetchWorldwideHistory()

    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(view!!.context)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        dashboardRecyclerViewAdapter = DashboardRecyclerViewAdapter(view!!.context)
        rv_top_country.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = dashboardRecyclerViewAdapter
        }
        dashboardRecyclerViewAdapter.itemClickListener = object :
            BaseRecyclerViewAdapter.ItemClickListener<CountryResponse> {
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
    private val topCountryListObserver = Observer<ArrayList<CountryResponse>> {
        it?.let {
            dashboardRecyclerViewAdapter.setDataList(it)
        }
    }


    private fun setupLineChart() {
        chart_daily_case.gradientFillColors =
            intArrayOf(
                Color.parseColor("#e6f2ff"),
                Color.TRANSPARENT
            )
        chart_daily_case.animation.duration = ANIMATION_DURATION


        chart_daily_recovered.gradientFillColors =
            intArrayOf(
                Color.parseColor("#e9faee"),
                Color.TRANSPARENT
            )
        chart_daily_recovered.animation.duration = ANIMATION_DURATION


        chart_daily_deaths.gradientFillColors =
            intArrayOf(
                Color.parseColor("#ffeff2"),
                Color.TRANSPARENT
            )
        chart_daily_deaths.animation.duration = ANIMATION_DURATION
    }

    private fun fetchTopCountryList() {
        dashboardViewModel.fetchCountryList().observe(this, countryListObserver)
    }

    private fun fetchWorldwideData() {
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchWorldwideData().observe(this, worldwideDataObserver)
    }

    private fun fetchWorldwideHistory() {
        dashboardViewModel.fetchWorldwideHistory().observe(this, worldwideHistoryObserver)
    }

    private fun fetchVietnamData() {
        dashboardViewModel.fetchVietnamData().observe(this, vietnamDataObserver)
    }

    private fun fetchVietnamHistory() {
        dashboardViewModel.fetchVietnamHistory().observe(this, vietnamHistoryObserver)
    }

    private val worldwideDataObserver = Observer<WorldwideResponse> {
        it?.let {
            hideDialog()
            tv_update_time.text =
                "Last update ${AppUtil.convertMillisecondsToDateFormat(it.updated)}"
            tv_total_cases_count.text = AppUtil.toNumberWithCommas(it.cases)
            tv_total_recovered_count.text = AppUtil.toNumberWithCommas(it.recovered)
            tv_total_death_count.text = AppUtil.toNumberWithCommas(it.deaths)
            tv_today_cases_count.text = "(+${AppUtil.toNumberWithCommas(it.todayCases)})"
            tv_today_recovered_count.text = "(+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())})"
            tv_today_deaths_count.text = "(+${AppUtil.toNumberWithCommas(it.todayDeaths)})"

            tv_cases_1_count.text = "+${AppUtil.toNumberWithCommas(it.todayCases)}"
            tv_recover_1_count.text = "+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())}"
            tv_deaths_1_count.text = "+${AppUtil.toNumberWithCommas(it.todayDeaths)}"
        }
    }

    private val worldwideHistoryObserver = Observer<HistoricalWorldwideResponse> {
        hideDialog()
        it.let {
            chart_daily_case.animate(
                getNewCaseList(
                    it.cases.toList().sortedBy { value -> value.second }
                )
            )
            chart_daily_recovered.animate(
                getNewCaseList(
                    it.recovered.toList().sortedBy { value -> value.second }
                )
            )
            chart_daily_deaths.animate(
                getNewCaseList(
                    it.deaths.toList().sortedBy { value -> value.second }
                )
            )
        }
    }

    private val vietnamDataObserver = Observer<CountryResponse> {
        tv_update_time.text =
            "Last update ${AppUtil.convertMillisecondsToDateFormat(it.updated)}"
        tv_total_cases_count.text = AppUtil.toNumberWithCommas(it.cases.toLong())
        tv_total_recovered_count.text = AppUtil.toNumberWithCommas(it.recovered.toLong())
        tv_total_death_count.text = AppUtil.toNumberWithCommas(it.deaths.toLong())
        tv_today_cases_count.text = "(+${AppUtil.toNumberWithCommas(it.todayCases.toLong())})"
        tv_today_recovered_count.text = "(+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())})"
        tv_today_deaths_count.text = "(+${AppUtil.toNumberWithCommas(it.todayDeaths.toLong())})"

        tv_cases_1_count.text = "+${AppUtil.toNumberWithCommas(it.todayCases.toLong())}"
        tv_recover_1_count.text = "+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())}"
        tv_deaths_1_count.text = "+${AppUtil.toNumberWithCommas(it.todayDeaths.toLong())}"

    }

    private val vietnamHistoryObserver = Observer<HistoricalVietnamResponse> {
        hideDialog()
        it.let {
            chart_daily_case.animate(
                getNewCaseList(
                    it.timeline.cases.toList().sortedBy { value -> value.second }
                )
            )
            chart_daily_recovered.animate(
                getNewCaseList(
                    it.timeline.recovered.toList().sortedBy { value -> value.second }
                )
            )
            chart_daily_deaths.animate(
                getNewCaseList(
                    it.timeline.deaths.toList().sortedBy { value -> value.second }
                )
            )
        }
    }

    private val countryListObserver = Observer<ArrayList<CountryResponse>> {
        it?.let {
            dashboardRecyclerViewAdapter.setDataList(it)
        }
    }

    private fun getNewCaseList(totalCaseList: List<Pair<String, Long>>): List<Pair<String, Float>> {
        val newCaseList = ArrayList<Pair<String, Float>>()
        for (i in 1 until totalCaseList.size) {
            val difference = totalCaseList[i].second - totalCaseList[i - 1].second
            val newCase = Pair(totalCaseList[i].first, difference.toFloat())
            newCaseList.add(newCase)
        }
        return newCaseList
    }

    private fun toggleWorldwideSwitch() {
        sw_worldwide.setOnCheckedChangeListener { _, isChecked ->
            showProgressDialog("Fetching Data")
            if (isChecked) {
                fetchVietnamData()
                fetchVietnamHistory()
            } else {
                fetchWorldwideData()
                fetchWorldwideHistory()

            }
        }
    }
}