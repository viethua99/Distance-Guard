package com.thesis.distanceguard.presentation.dashboard

import android.graphics.Color
import android.util.Log
import android.view.View
import com.db.williamchart.slidertooltip.SliderTooltip
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.TotalResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.information.InformationFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import kotlinx.android.synthetic.main.fragment_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : BaseFragment() {
    companion object {
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


        private val donutSet = listOf(
            20f,
            80f,
            100f
        )
    }

    override fun getResLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onMyViewCreated(view: View) {
        getDataCovidFromApi()
        testLineChart()
        testBarChart()
     //   testDonutChart()

        btn_click_here.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.addFragment(
                InformationFragment(),
                InformationFragment.TAG,
                R.id.container_main
            )
        }
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

    private fun testDonutChart() {
        donutChart.donutColors = intArrayOf(
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#9EFFFFFF"),
            Color.parseColor("#8DFFFFFF")
        )
        donutChart.animation.duration = animationDuration
        donutChart.animate(donutSet)

    }


    private fun prepareUpdateCovid(comfirmed: Long, recovered: Long, deaths: Long) {
        tv_confirm_count.text = "" + comfirmed
        tv_recovered_count.text = "" + recovered
        tv_death_count.text = "" + deaths
    }

    private fun getDataCovidFromApi() {
        CovidService.getApi().getAll().enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>, response: Response<TotalResponse>) {
                response.let {
                    val resp = it.body()
                    Log.d("TAG", "onResponse: " + resp?.cases)
                    prepareUpdateCovid(resp!!.cases, resp?.recovered, resp?.deaths)
                }
            }

            override fun onFailure(call: Call<TotalResponse>, t: Throwable) {
            }
        })
    }
}