package com.thesis.distanceguard.presentation.dashboard

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.db.williamchart.slidertooltip.SliderTooltip
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.model.CountryInfoResponse
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalAllResponse
import com.thesis.distanceguard.api.model.TotalResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.information.InformationFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

class DashboardFragment : BaseFragment() {
    companion object {
        const val TAG = "DashboardFragment"
        private const val animationDuration = 1000L

        private val barSet = listOf(
            "JAN" to 8F,
            "FEB" to 8F,
            "MAR" to 8F,
            "MAY" to 8F,
            "APR" to 8F,
            "JUN" to 8F,
            "JAN" to 8F

        )
    }

    private lateinit var dashboardViewModel: DashboardViewModel


    override fun getResLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onMyViewCreated(view: View) {
        setupViewModel()
        fetchCountryVietNam()
        fetchHistoricalVietNam()
        setupLineChart()
        excuteSwitch()
        btn_click_here.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.addFragment(
                InformationFragment(),
                InformationFragment.TAG,
                R.id.container_main
            )
        }
    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        dashboardViewModel =
            ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
    }

    private fun setupLineChart() {
        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#e6f2ff"),
                Color.TRANSPARENT
            )
        lineChart.animation.duration = animationDuration
        lineChart.tooltip =
            SliderTooltip().also {
                it.color = Color.BLUE
            }
        lineChart.onDataPointTouchListener = { index, _, _ ->

        }

        lineChart2.gradientFillColors =
            intArrayOf(
                Color.parseColor("#e9faee"),
                Color.TRANSPARENT
            )
        lineChart2.animation.duration = animationDuration
        lineChart2.tooltip =
            SliderTooltip().also {
                it.color = Color.GREEN
            }
        lineChart2.onDataPointTouchListener = { index, _, _ ->

        }

        lineChart3.gradientFillColors =
            intArrayOf(
                Color.parseColor("#ffeff2"),
                Color.TRANSPARENT
            )
        lineChart3.animation.duration = animationDuration
        lineChart3.tooltip =
            SliderTooltip().also {
                it.color = Color.RED
            }
        lineChart3.onDataPointTouchListener = { index, _, _ ->

        }
    }

    private fun fetchTotalCases() {
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchTotalCases().observe(this, totalCasesObserver)
    }

    private fun fetchHistorical() {
        dashboardViewModel.fetchHistory().observe(this, historyObserver)
    }

    private fun fetchCountryVietNam() {
        dashboardViewModel.fetchVietNam().observe(this, countryVietNamObserver)
    }

    private fun fetchHistoricalVietNam() {
        dashboardViewModel.fetchHistoryVietNam().observe(this, historyVietNamObserver)
    }

    private val totalCasesObserver = Observer<TotalResponse> {
        it?.let {
            hideDialog()
            tv_update_time.text =
                "Last update ${AppUtil.convertMillisecondsToDateFormat(it.updated)}"
            tv_total_cases_count.text = AppUtil.toNumberWithCommas(it.cases)
            tv_total_recovered_count.text = AppUtil.toNumberWithCommas(it.recovered)
            tv_total_death_count.text = AppUtil.toNumberWithCommas(it.deaths)
            tv_today_cases_count.text = "(+${AppUtil.toNumberWithCommas(it.todayCases)})"
            tv_today_recovered_count.text =
                "(+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())})"
            tv_today_deaths_count.text = "(+${AppUtil.toNumberWithCommas(it.todayDeaths)})"
        }
    }

    private val historyObserver = Observer<HistoricalAllResponse> {
        setDataLineChart(it)
    }

    private val countryVietNamObserver = Observer<CountryResponse> {
        tv_update_time.text =
            "Last update ${AppUtil.convertMillisecondsToDateFormat(it.updated)}"
        tv_total_cases_count.text = AppUtil.toNumberWithCommas(it.cases.toLong())
        tv_total_recovered_count.text = AppUtil.toNumberWithCommas(it.recovered.toLong())
        tv_total_death_count.text = AppUtil.toNumberWithCommas(it.deaths.toLong())
        tv_today_cases_count.text = "(+${AppUtil.toNumberWithCommas(it.todayCases.toLong())})"
        tv_today_recovered_count.text =
            "(+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())})"
        tv_today_deaths_count.text = "(+${AppUtil.toNumberWithCommas(it.todayDeaths.toLong())})"
    }

    private val historyVietNamObserver = Observer<HistoricalAllResponse> {
        setDataLineChart(it)
    }

    private fun getNewCaseList(totalCaseList: List<Pair<String, Float>>): List<Pair<String, Float>> {
        val newCaseList = ArrayList<Pair<String, Float>>()
        for (i in 1 until totalCaseList.size) {
            val difference = totalCaseList[i].second - totalCaseList[i - 1].second
            val newCase = Pair(totalCaseList[i].first, difference)
            newCaseList.add(newCase)
        }
        return newCaseList
    }

    private fun excuteSwitch() {
        toggleAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                fetchTotalCases()
                fetchHistorical()
            } else {
                fetchCountryVietNam()
                fetchHistoricalVietNam()
            }
        }
    }

    private fun setDataLineChart(it: HistoricalAllResponse) {
        it.let {
            lineChart.animate(
                getNewCaseList(
                    it.cases!!.toList()?.sortedBy { value -> value.second }?.toList()
                )
            )
            lineChart2.animate(
                getNewCaseList(
                    it.recovered!!.toList()?.sortedBy { value -> value.second }?.toList()
                )
            )
            lineChart3.animate(
                getNewCaseList(
                    it.deaths!!.toList()?.sortedBy { value -> value.second }?.toList()
                )
            )
        }
    }
}