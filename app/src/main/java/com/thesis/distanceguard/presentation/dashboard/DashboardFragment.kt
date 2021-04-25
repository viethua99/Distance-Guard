package com.thesis.distanceguard.presentation.dashboard

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.db.williamchart.slidertooltip.SliderTooltip
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.model.TotalResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.countries.CountriesViewModel
import com.thesis.distanceguard.presentation.information.InformationFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.NumberUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class DashboardFragment : BaseFragment() {
    companion object {
        private const val animationDuration = 1000L
        private val lineSet = listOf(
            "label1" to 5f,
            "label2" to 4.5f,
            "label3" to 4.7f,
            "label4" to 3.5f,
            "label5" to 3.6f,
            "label6" to 7.5f,
            "label7" to 7.5f,
            "label8" to 10f,
            "label9" to 5f,
            "label10" to 6.5f,
            "label11" to 3f,
            "label12" to 4f
        )
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
        fetchTotalCases()
        testLineChart()
        testBarChart()

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
        dashboardViewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
    }

    private fun fetchTotalCases(){
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchTotalCases().observe(this,totalCasesObserver)
    }

    private fun testLineChart() {
        /**
         * Line Chart
         */
        lineChart.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FF4B63"),
                Color.TRANSPARENT
            )
        lineChart.animation.duration = animationDuration
        lineChart.tooltip =
            SliderTooltip().also {
                it.color = Color.RED
            }
        lineChart.onDataPointTouchListener = { index, _, _ ->

        }
        lineChart.animate(lineSet)
    }

    private fun testBarChart() {
        barChart.animation.duration = animationDuration
        barChart.animate(barSet)
    }


    private val totalCasesObserver = Observer<TotalResponse>{
        it?.let {
            hideDialog()
            tv_total_cases_count.text = NumberUtil.toNumberWithCommas(it.cases)
            tv_total_recovered_count.text = NumberUtil.toNumberWithCommas(it.recovered)
            tv_total_death_count.text = NumberUtil.toNumberWithCommas(it.deaths)
            tv_today_cases_count.text =  "(+${NumberUtil.toNumberWithCommas(it.todayCases)})"
            tv_today_recovered_count.text = "(+${NumberUtil.toNumberWithCommas(it.todayRecovered.toLong())})"
            tv_today_deaths_count.text = "(+${NumberUtil.toNumberWithCommas(it.todayDeaths)})"

        }
    }

}