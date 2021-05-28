package com.thesis.distanceguard.presentation.dashboard

import android.content.Context
import android.net.ConnectivityManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.github.mikephil.charting.utils.EntryXComparator
import com.thesis.distanceguard.R
import com.thesis.distanceguard.model.ChartDate
import com.thesis.distanceguard.model.ChartType
import com.thesis.distanceguard.model.DashboardMode
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.information.InformationFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.presentation.map.MapFragment
import com.thesis.distanceguard.room.entities.CountryEntity
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity
import com.thesis.distanceguard.room.entities.HistoricalWorldwideEntity
import com.thesis.distanceguard.room.entities.WorldwideEntity
import com.thesis.distanceguard.util.AppUtil
import com.thesis.distanceguard.util.DateValueFormatter
import com.thesis.distanceguard.util.MyMarkerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber
import java.util.*


class DashboardFragment : BaseFragment() {


    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var dashboardRecyclerViewAdapter: DashboardRecyclerViewAdapter
    override fun getResLayoutId(): Int {
        return R.layout.fragment_dashboard
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)
        setupViewModel()
        setupViews()
        fetchInitData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_reload -> {
                showProgressDialog("Fetching Data")
                dashboardViewModel.reloadData()
                checkAllChipVisibility()
            }

            R.id.item_map -> {
                (activity as MainActivity).addFragment(
                    MapFragment(),
                    MapFragment.TAG,
                    R.id.container_main
                )
            }
        }
        return true
    }


    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidSupportInjection.inject(this)
        dashboardViewModel =
            ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
        dashboardViewModel.worldwideResponse.observe(this, worldwideDataObserver)
        dashboardViewModel.vietnamResponse.observe(this, vietnamDataObserver)
        dashboardViewModel.historicalWorldwideResponse.observe(this, worldwideHistoryObserver)
        dashboardViewModel.historicalVietnamResponse.observe(this, vietnamHistoryObserver)
    }

    private fun setupViews() {
        tv_chart_type.setOnClickListener {
            showChartTypeMenu()
        }

        chip_group_filters.setOnCheckedChangeListener { _, checkedId ->
            showProgressDialog("Fetching Data")
            when (checkedId) {
                R.id.chip_thirty_days -> {
                    dashboardViewModel.fetchChartByDate(ChartDate.THIRTY_DAYS)
                }
                else -> {
                    dashboardViewModel.fetchChartByDate(ChartDate.ALL_TIME)

                }
            }
        }

        checkAllChipVisibility()
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            activity!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun checkAllChipVisibility() {
        if (!isNetworkAvailable()) {
            chip_all.visibility = View.GONE
        } else {
            chip_all.visibility = View.VISIBLE
        }
    }

    private fun fetchInitData() {
        showProgressDialog("Fetching Data")
        dashboardViewModel.fetchDashboardData(DashboardMode.WORLDWIDE)
        dashboardViewModel.fetchCountryList().observe(this, countryListObserver)


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
            BaseRecyclerViewAdapter.ItemClickListener<CountryEntity> {
            override fun onClick(position: Int, item: CountryEntity) {
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

            override fun onLongClick(position: Int, item: CountryEntity) {}
        }
    }

    private fun setupLineChart() {
        val mv = MyMarkerView(activity, R.layout.custom_marker_view)
        chart_spread.marker = mv
        chart_spread.legend.textColor = ContextCompat.getColor(context!!, R.color.primary_color)
        chart_spread.description = null
        chart_spread.axisRight.isEnabled = false
        chart_spread.axisLeft.textColor = ContextCompat.getColor(context!!, R.color.primary_color)
        chart_spread.axisLeft.setDrawGridLines(false)
        chart_spread.xAxis.setDrawGridLines(false)
        chart_spread.xAxis.isEnabled = true
        chart_spread.xAxis.textColor = ContextCompat.getColor(context!!, R.color.primary_color)
        chart_spread.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_spread.xAxis.valueFormatter = DateValueFormatter()
        chart_spread.setExtraOffsets(0f, 0f, 0f, 15f)
    }

    private fun showChartTypeMenu() {
        val popupMenu = PopupMenu(context, tv_chart_type)
        popupMenu.menuInflater.inflate(R.menu.menu_chart_type, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            showProgressDialog("Fetching Data")
            when (it.itemId) {
                R.id.item_daily -> {
                    tv_chart_type.text = "Daily"
                    dashboardViewModel.fetchChartDataByType(ChartType.DAILY)
                }
                R.id.item_cumulative -> {
                    tv_chart_type.text = "Cumulative"
                    dashboardViewModel.fetchChartDataByType(ChartType.CUMULATIVE)

                }
            }

            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
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
                dashboardViewModel.fetchDashboardData(DashboardMode.VIETNAM)
            } else {
                dashboardViewModel.fetchDashboardData(DashboardMode.WORLDWIDE)
            }
        }
    }


    private fun setDailyChart(response: HistoricalWorldwideEntity?) {
        response?.let {
            val cases = getNewCaseList(it.cases!!.toList().sortedBy { value -> value.second })
            val casesEntries = mutableListOf<Entry>()
            cases.forEachIndexed { _, pair ->
                val first = pair.first

                val mill = AppUtil.convertStringDateToMillisecond(pair.first)
                val time = AppUtil.convertMillisecondsToDateFormat(mill)
                Timber.d("testt: first=$first --- mill=$mill -- time=$time")
                casesEntries.add(Entry(AppUtil.convertStringDateToMillisecond(pair.first).toFloat(), pair.second))
            }
            casesEntries.sortWith(EntryXComparator())

            val recovered =
                getNewCaseList(it.recovered!!.toList().sortedBy { value -> value.second })
            val recoveredEntries = mutableListOf<Entry>()
            recovered.forEachIndexed { _, pair ->
                recoveredEntries.add(Entry(AppUtil.convertStringDateToMillisecond(pair.first).toFloat(), pair.second))
            }
            recoveredEntries.sortWith(EntryXComparator())


            val deaths = getNewCaseList(it.deaths!!.toList().sortedBy { value -> value.second })
            val deathsEntries = mutableListOf<Entry>()
            deaths.forEachIndexed { _, pair ->
                deathsEntries.add(Entry(AppUtil.convertStringDateToMillisecond(pair.first).toFloat(), pair.second))
            }

            deathsEntries.sortWith(EntryXComparator())


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
                chart_spread.clear()
                chart_spread.resetZoom()

            }

            chart_spread.data = LineData(lines).apply {
                setDrawValues(false)
            }

            chart_spread.data.notifyDataChanged()
            chart_spread.invalidate()
        }
    }

    private fun setCumulativeChart(response: HistoricalWorldwideEntity?) {
        response?.let {
            val cases = it.cases!!.toList().sortedBy { value -> value.second }
            val casesEntries = mutableListOf<Entry>()
            cases.forEachIndexed { _, pair ->
                casesEntries.add(Entry(AppUtil.convertStringDateToMillisecond(pair.first).toFloat(), pair.second.toFloat()))
            }
            casesEntries.sortWith(EntryXComparator())


            val recovered = it.recovered!!.toList().sortedBy { value -> value.second }
            val recoveredEntries = mutableListOf<Entry>()
            recovered.forEachIndexed { _, pair ->
                recoveredEntries.add(Entry(AppUtil.convertStringDateToMillisecond(pair.first).toFloat(), pair.second.toFloat()))
            }
            recoveredEntries.sortWith(EntryXComparator())


            val deaths = it.deaths!!.toList().sortedBy { value -> value.second }
            val deathsEntries = mutableListOf<Entry>()
            deaths.forEachIndexed { _, pair ->
                deathsEntries.add(Entry(AppUtil.convertStringDateToMillisecond(pair.first).toFloat(), pair.second.toFloat()))
            }
            deathsEntries.sortWith(EntryXComparator())


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
                chart_spread.clear()
                chart_spread.resetZoom()
            }

            chart_spread.data = LineData(lines).apply {
                setDrawValues(false)
            }

            chart_spread.data.notifyDataChanged()
            chart_spread.invalidate()
        }
    }


    private val worldwideDataObserver = Observer<WorldwideEntity?> {
        it?.let {
            activity?.runOnUiThread {
                hideDialog()
                tv_update_time.text =
                    "Last update ${AppUtil.convertMillisecondsToDateFormat(it.updated!!)}"
                tv_total_cases_count.text = AppUtil.toNumberWithCommas(it.cases!!)
                tv_total_recovered_count.text = AppUtil.toNumberWithCommas(it.recovered!!)
                tv_total_death_count.text = AppUtil.toNumberWithCommas(it.deaths!!)
                tv_today_cases_count.text = "(+${AppUtil.toNumberWithCommas(it.todayCases!!)})"
                tv_today_recovered_count.text =
                    "(+${AppUtil.toNumberWithCommas(it.todayRecovered!!.toLong())})"
                tv_today_deaths_count.text = "(+${AppUtil.toNumberWithCommas(it.todayDeaths!!)})"

            }

        }
    }

    private val worldwideHistoryObserver = Observer<HistoricalWorldwideEntity?> {
        Timber.d("worldwideHistoryObserver")
        hideDialog()
        when (dashboardViewModel.chartType.value) {
            ChartType.DAILY -> {
                setDailyChart(it)
            }
            ChartType.CUMULATIVE -> {
                setCumulativeChart(it)
            }
        }
    }

    private val vietnamDataObserver = Observer<CountryEntity?> {
        it?.let { countryEntity ->
            tv_update_time.text =
                "Last update ${AppUtil.convertMillisecondsToDateFormat(countryEntity.updated!!)}"
            tv_total_cases_count.text = AppUtil.toNumberWithCommas(countryEntity.cases!!.toLong())
            tv_total_recovered_count.text =
                AppUtil.toNumberWithCommas(countryEntity.recovered!!.toLong())
            tv_total_death_count.text = AppUtil.toNumberWithCommas(countryEntity.deaths!!.toLong())
            tv_today_cases_count.text =
                "(+${AppUtil.toNumberWithCommas(countryEntity.todayCases!!.toLong())})"
            tv_today_recovered_count.text =
                "(+${AppUtil.toNumberWithCommas(countryEntity.todayRecovered!!.toLong())})"
            tv_today_deaths_count.text =
                "(+${AppUtil.toNumberWithCommas(countryEntity.todayDeaths!!.toLong())})"

        }

    }

    private val vietnamHistoryObserver = Observer<HistoricalCountryEntity?> {
        hideDialog()
        if (it == null) {
            if (chart_spread.data != null) {
                chart_spread.clearValues()
                chart_spread.clear()
            }
        }
        it?.let {
            val timeline = it.timeline
            when (dashboardViewModel.chartType.value) {
                ChartType.DAILY -> {
                    setDailyChart(timeline!!)
                }
                ChartType.CUMULATIVE -> {
                    setCumulativeChart(timeline!!)
                }
            }
        }
    }

    private val countryListObserver = Observer<ArrayList<CountryEntity>> {
        it?.let {
            dashboardRecyclerViewAdapter.setDataList(it)
        }
    }


}