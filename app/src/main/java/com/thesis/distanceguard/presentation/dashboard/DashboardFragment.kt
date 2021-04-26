package com.thesis.distanceguard.presentation.dashboard

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.db.williamchart.slidertooltip.SliderTooltip
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.CovidService
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
        private var lineSet = listOf(
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
        fetch()
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

    private fun fetchTotalCases() {
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchTotalCases().observe(this, totalCasesObserver)
    }

    private fun testLineChart() {
        /**
         * Line Chart
         */
        lineChartT.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FF4B63"),
                Color.TRANSPARENT
            )
        lineChartT.animation.duration = animationDuration
        lineChartT.tooltip =
            SliderTooltip().also {
                it.color = Color.RED
            }
        lineChartT.onDataPointTouchListener = { index, _, _ ->

        }

        //
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
//        lineChart.animate(lineSet)

        // Deaths
        lineChartDeaths.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FF4B63"),
                Color.TRANSPARENT
            )
        lineChartDeaths.animation.duration = animationDuration
        lineChartDeaths.tooltip =
            SliderTooltip().also {
                it.color = Color.RED
            }
        lineChartDeaths.onDataPointTouchListener = { index, _, _ ->

        }
//        lineChartDeaths.animate(lineSet)

        //Recovered
        lineChartRecovered.gradientFillColors =
            intArrayOf(
                Color.parseColor("#FF4B63"),
                Color.TRANSPARENT
            )
        lineChartRecovered.animation.duration = animationDuration
        lineChartRecovered.tooltip =
            SliderTooltip().also {
                it.color = Color.RED
            }
        lineChartRecovered.onDataPointTouchListener = { index, _, _ ->

        }
//        lineChartRecovered.animate(lineSet)

    }

    private fun testBarChart() {
        barChart.animation.duration = animationDuration
        barChart.animate(barSet)
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

    private fun fetch() {
        CovidService.getApi().getHistoricalAll().enqueue(object : Callback<HistoricalAllResponse> {
            override fun onResponse(
                call: Call<HistoricalAllResponse>,
                response: Response<HistoricalAllResponse>
            ) {


                Log.d(
                    TAG,
                    "onResponse: " + response.body()?.cases?.toList()
                        ?.sortedByDescending { it.second })
                response.body()?.cases?.toList()?.sortedByDescending { it.second }?.toList()?.let {
                    lineChart.animate(
                        it
                    )
                }
                response.body()?.deaths?.toList()?.sortedByDescending { it.second }?.toList()
                    ?.let { lineChartDeaths.animate(it) }
                response.body()?.recovered?.toList()?.sortedByDescending { it.second }?.toList()
                    ?.let {
                        lineChartRecovered.animate(
                            it
                        )
                    }

                lineChart.invalidate()
                lineChartDeaths.invalidate()
                lineChartRecovered.invalidate()
            }

            override fun onFailure(call: Call<HistoricalAllResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: " + t.message)
            }

        })
    }
}