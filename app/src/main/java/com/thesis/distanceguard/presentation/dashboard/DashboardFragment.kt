package com.thesis.distanceguard.presentation.dashboard

import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.*
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.information.InformationFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.util.AppUtil
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber
import java.util.*

class DashboardFragment : BaseFragment() {


    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dashboardRecyclerViewAdapter: DashboardRecyclerViewAdapter
    private var chartType = "Daily"
    private var lastdays = "30"
    private var country = "Worldwide"
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
        tv_chart_type.setOnClickListener {
            showChartTypeMenu()
        }

        chip_group_filters.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chip_thirty_days -> {
                    lastdays = "30"
                    if (country == "Worldwide") {
                        fetchWorldwideHistory(lastdays)
                    } else {
                        fetchVietnamHistory(lastdays)
                    }
                }
                else -> {
                    lastdays = "all"
                    if (country == "Worldwide") {
                        fetchWorldwideHistory(lastdays)
                    } else {
                        fetchVietnamHistory(lastdays)
                    }
                }
            }
        }

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
        fetchWorldwideHistory("30")

        dashboardViewModel.errorMessage.observe(this, Observer {
            hideDialog()
            showToastMessage(it)
        })

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
                ViewCompat.postOnAnimationDelayed(
                    view!!, // Delay to show ripple effect
                    Runnable {
                        val mainActivity = activity as MainActivity
                        mainActivity.addFragment(
                            DetailFragment(item),
                            DetailFragment.TAG,
                            R.id.container_main
                        )
                    }, 50
                )
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
        chart_spread.legend.textColor = ContextCompat.getColor(context!!, R.color.primary_color)
        chart_spread.xAxis.textColor = ContextCompat.getColor(context!!, R.color.primary_color)
        chart_spread.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.primary_color)
        chart_spread.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_spread.description = null
        chart_spread.axisRight.isEnabled = false
        chart_spread.xAxis.isEnabled = false
        chart_spread.axisLeft.setDrawGridLines(false)
        chart_spread.xAxis.setDrawGridLines(false)
//        chart_spread.xAxis.valueFormatter = MyXAxisValueFormatter()
        chart_spread.setExtraOffsets(0f, 0f, 0f, 15f)
    }

    private fun showChartTypeMenu() {
        val popupMenu = PopupMenu(context, tv_chart_type)
        popupMenu.menuInflater.inflate(R.menu.menu_chart_type, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_daily -> {
                    tv_chart_type.text = "Daily"
                    chartType = "Daily"
                    if (country == "Worldwide") {
                        fetchWorldwideHistory(lastdays)

                    } else {
                        fetchVietnamHistory(lastdays)
                    }
                }
                R.id.item_cumulative -> {
                    tv_chart_type.text = "Cumulative"
                    chartType = "Cumulative"
                    if (country == "Worldwide") {
                        fetchWorldwideHistory(lastdays)

                    } else {
                        fetchVietnamHistory(lastdays)
                    }
                }
            }

            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }


    private fun fetchTopCountryList() {
        dashboardViewModel.fetchCountryList().observe(this, countryListObserver)
    }

    private fun fetchWorldwideData() {
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchWorldwideData().observe(this, worldwideDataObserver)
    }

    private fun fetchWorldwideHistory(lastdays: String) {
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchWorldwideHistory(lastdays).observe(this, worldwideHistoryObserver)
    }

    private fun fetchVietnamData() {
        dashboardViewModel.fetchVietnamData().observe(this, vietnamDataObserver)
    }

    private fun fetchVietnamHistory(lastdays: String) {
        showProgressDialog("Fetching data")
        dashboardViewModel.fetchVietnamHistory(lastdays).observe(this, vietnamHistoryObserver)
    }

    private fun setDailyChart(response: HistoricalWorldwideResponse) {
        val cases = getNewCaseList(response.cases.toList().sortedBy { value -> value.second })
        val casesEntries = mutableListOf<Entry>()
        cases.forEachIndexed { index, pair ->
            if (pair.second > 1000000) {
                Timber.d(pair.toString())
            }
            casesEntries.add(Entry(index.toFloat(), pair.second))
        }

        val recovered =
            getNewCaseList(response.recovered.toList().sortedBy { value -> value.second })
        val recoveredEntries = mutableListOf<Entry>()
        recovered.forEachIndexed { index, pair ->
            recoveredEntries.add(Entry(index.toFloat(), pair.second))
        }

        val deaths = getNewCaseList(response.deaths.toList().sortedBy { value -> value.second })
        val deathsEntries = mutableListOf<Entry>()
        deaths.forEachIndexed { index, pair ->
            deathsEntries.add(Entry(index.toFloat(), pair.second))
        }

        val lines = arrayListOf<ILineDataSet>().apply {
            add(
                LineDataSet(
                    casesEntries,
                    "cases"
                ).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.primary_color)
                    setDrawCircles(false)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.10f
                })

            add(
                LineDataSet(
                    recoveredEntries,
                    "recovered"
                ).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.primary_green)
                    setDrawCircles(false)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.10f
                })

            add(
                LineDataSet(
                    deathsEntries,
                    "deaths"
                ).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.primary_red)
                    setDrawCircles(false)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.10f
                })
        }

        if (chart_spread.data != null) {
            chart_spread.clearValues()
        }

        chart_spread.data = LineData(lines).apply {
            setDrawValues(false)
        }

        chart_spread.data.notifyDataChanged()
        chart_spread.invalidate()
    }

    private fun setCumulativeChart(response: HistoricalWorldwideResponse) {
        val cases = response.cases.toList().sortedBy { value -> value.second }
        val casesEntries = mutableListOf<Entry>()
        cases.forEachIndexed { index, pair ->
            if (pair.second > 1000000) {
                Timber.d(pair.toString())
            }
            casesEntries.add(Entry(index.toFloat(), pair.second.toFloat()))
        }

        val recovered = response.recovered.toList().sortedBy { value -> value.second }
        val recoveredEntries = mutableListOf<Entry>()
        recovered.forEachIndexed { index, pair ->
            recoveredEntries.add(Entry(index.toFloat(), pair.second.toFloat()))
        }

        val deaths = response.deaths.toList().sortedBy { value -> value.second }
        val deathsEntries = mutableListOf<Entry>()
        deaths.forEachIndexed { index, pair ->
            deathsEntries.add(Entry(index.toFloat(), pair.second.toFloat()))
        }

        val lines = arrayListOf<ILineDataSet>().apply {
            add(
                LineDataSet(
                    casesEntries,
                    "cases"
                ).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.primary_color)
                    setDrawCircles(false)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.10f
                })

            add(
                LineDataSet(
                    recoveredEntries,
                    "recovered"
                ).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.primary_green)
                    setDrawCircles(false)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.10f
                })

            add(
                LineDataSet(
                    deathsEntries,
                    "deaths"
                ).apply {
                    color = ContextCompat.getColor(requireContext(), R.color.primary_red)
                    setDrawCircles(false)
                    lineWidth = 2f
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.10f
                })
        }

        if (chart_spread.data != null) {
            chart_spread.clearValues()
        }

        chart_spread.data = LineData(lines).apply {
            setDrawValues(false)
        }

        chart_spread.data.notifyDataChanged()
        chart_spread.invalidate()
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
            tv_today_recovered_count.text =
                "(+${AppUtil.toNumberWithCommas(it.todayRecovered.toLong())})"
            tv_today_deaths_count.text = "(+${AppUtil.toNumberWithCommas(it.todayDeaths)})"

        }
    }

    private val worldwideHistoryObserver = Observer<HistoricalWorldwideResponse> {
        Timber.d("worldwideHistoryObserver")
        hideDialog()
        when (chartType) {
            "Daily" -> {
                setDailyChart(it)
            }
            else -> {
                setCumulativeChart(it)
            }
        }
    }

    private val vietnamDataObserver = Observer<CountryResponse> {
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

    private val vietnamHistoryObserver = Observer<HistoricalCountryResponse> {
        hideDialog()
        it.let {
            val timeline = it.timeline
            when (chartType) {
                "Daily" -> {
                    setDailyChart(timeline)
                }
                else -> {
                    setCumulativeChart(timeline)
                }
            }
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
            if (difference <= 1000000) {
                val newCase = Pair(totalCaseList[i].first, difference.toFloat())
                newCaseList.add(newCase)
            }
        }
        return newCaseList
    }

    private fun toggleWorldwideSwitch() {
        sw_worldwide.setOnCheckedChangeListener { _, isChecked ->
            showProgressDialog("Fetching Data")
            if (isChecked) {
                country = "Vietnam"
                fetchVietnamData()
                fetchVietnamHistory(lastdays)
            } else {
                country = "Worldwide"
                fetchWorldwideData()
                fetchWorldwideHistory(lastdays)

            }
        }
    }
}