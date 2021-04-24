package com.thesis.distanceguard.presentation.dashboard

import android.graphics.Color
import android.util.Log
import android.view.View
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.WorldResponse
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.MainActivity
import com.thesis.distanceguard.presentation.map.MapFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : BaseFragment() {
    override fun getResLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onMyViewCreated(view: View) {
        showPieChart()
        getDataCovidFromApi()
        showLineChart()
    }

    private fun showPieChart() {
        val pieEntries: ArrayList<PieEntry> = ArrayList()
        val label = "type"

        //initializing data
        val typeAmountMap: MutableMap<String, Int> = HashMap()
        typeAmountMap["Confirm"] = 200
        typeAmountMap["Recovered"] = 230
        typeAmountMap["Deaths"] = 100

        //initializing colors for the entries
        val colors: ArrayList<Int> = ArrayList()
        colors.add(Color.parseColor("#fd9555"))
        colors.add(Color.parseColor("#00b871"))
        colors.add(Color.parseColor("#ff3839"))

        //input data and fit data into pie chart entry
        for (type in typeAmountMap.keys) {
            pieEntries.add(PieEntry(typeAmountMap[type]!!.toFloat(), type))
        }

        //collecting the entries with label name
        val pieDataSet = PieDataSet(pieEntries, label)
        //setting text size of the value
        pieDataSet.valueTextSize = 12f
        //providing color list for coloring different entries
        pieDataSet.colors = colors
        //grouping the data set from entry to chart
        val pieData = PieData(pieDataSet)
        //showing the value of the entries, default true if not set
        pieData.setDrawValues(true)
        pie_chart.data = pieData
        pie_chart.invalidate()
        fab_map.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.replaceFragment(MapFragment(), "Map Fragment", R.id.container_main)
        }
    }

    private fun showLineChart() {
        val xValue = ArrayList<Entry>()
        xValue.add(Entry(0F, 10F))
        xValue.add(Entry(0.5F, 20F))
        xValue.add(Entry(1F, 10F))
        xValue.add(Entry(1.5F, 30F))
        xValue.add(Entry(2F, 5F))
        xValue.add(Entry(2.5F, 10F))

        val lineDataSet = LineDataSet(xValue, "LINE CHARTT")

        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(lineDataSet)

        val data = LineData(dataSets)
        line_chart.data = data

        line_chart.setDrawGridBackground(false)
        line_chart.isDragEnabled = true
        line_chart.setScaleEnabled(true)
        line_chart.setPinchZoom(true)
        line_chart.invalidate()
    }

    private fun prepareUpdateCovid(comfirmed: Long, recovered: Long, deaths: Long) {
        tv_confirm_count.text = "" + comfirmed
        tv_recovered_count.text = "" + recovered
        tv_death_count.text = "" + deaths
    }

    private fun getDataCovidFromApi() {
        CovidService.getApi().getCovidWorld().enqueue(object : Callback<WorldResponse> {
            override fun onResponse(call: Call<WorldResponse>, response: Response<WorldResponse>) {
                response.let {
                    val resp = it.body()
                    Log.d("TAG", "onResponse: " + resp?.cases)
                    prepareUpdateCovid(resp!!.cases, resp?.recovered, resp?.deaths)
                }
            }
            override fun onFailure(call: Call<WorldResponse>, t: Throwable) {
            }
        })
    }
}