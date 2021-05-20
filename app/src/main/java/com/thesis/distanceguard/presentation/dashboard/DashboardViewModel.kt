package com.thesis.distanceguard.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.response.CountryResponse
import com.thesis.distanceguard.api.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.api.response.HistoricalCountryResponse
import com.thesis.distanceguard.api.response.WorldwideResponse
import com.thesis.distanceguard.model.ChartDate
import com.thesis.distanceguard.model.ChartType
import com.thesis.distanceguard.model.DashboardMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel @Inject constructor() : ViewModel() {
    val chartType = MutableLiveData<ChartType>(ChartType.DAILY)
    private val chartDate = MutableLiveData<ChartDate>(ChartDate.THIRTY_DAYS)
    private val dashboardMode = MutableLiveData<DashboardMode>(DashboardMode.WORLDWIDE)


    val worldwideResponse = MutableLiveData<WorldwideResponse>()
    val vietnamResponse = MutableLiveData<CountryResponse>()

    val historicalWorldwideResponse = MutableLiveData<HistoricalWorldwideResponse>()
    val historicalVietnamResponse = MutableLiveData<HistoricalCountryResponse>()

    val errorMessage = MutableLiveData<String>()
    val countryList = MutableLiveData<ArrayList<CountryResponse>>()

    fun fetchDashboardData(dashboardMode: DashboardMode) {
        this.dashboardMode.value = dashboardMode
        when (dashboardMode) {
            DashboardMode.WORLDWIDE -> {
                fetchWorldwideData()
                fetchWorldwideChartByDate(chartDate.value!!)
            }
            DashboardMode.VIETNAM -> {
                fetchVietnamData()
                fetchVietnamChartByDate(chartDate.value!!)
            }
        }
    }

    fun fetchChartByDate(chartDate: ChartDate) {
        this.chartDate.value = chartDate
        when (dashboardMode.value) {
            DashboardMode.WORLDWIDE -> {
                fetchWorldwideChartByDate(chartDate)

            }
            DashboardMode.VIETNAM -> {
                fetchVietnamChartByDate(chartDate)

            }
        }
    }

    fun fetchChartDataByType(chartType: ChartType){
        this.chartType.value = chartType
        when(chartType){
            ChartType.DAILY -> {
                fetchChartByDate(chartDate.value!!)
            }

            ChartType.CUMULATIVE -> {
                fetchChartByDate(chartDate.value!!)
            }
        }
    }

    private fun fetchWorldwideChartByDate(chartDate: ChartDate) {
        this.chartDate.value = chartDate
        when (chartDate) {
            ChartDate.THIRTY_DAYS -> {
                fetchWorldwideHistory("30")
            }
            ChartDate.ALL_TIME -> {
                fetchWorldwideHistory("all")
            }
        }
    }

    private fun fetchVietnamChartByDate(chartDate: ChartDate) {
        this.chartDate.value = chartDate
        when (chartDate) {
            ChartDate.THIRTY_DAYS -> {
                fetchVietnamHistory("30")
            }
            ChartDate.ALL_TIME -> {
                fetchVietnamHistory("all")
            }
        }
    }

    private fun fetchWorldwideData() {
        CovidService.getApi().getWorldwideData().enqueue(object : Callback<WorldwideResponse> {
            override fun onResponse(
                call: Call<WorldwideResponse>,
                response: Response<WorldwideResponse>
            ) {
                response.let {
                    if (it.isSuccessful) {
                        worldwideResponse.value = it.body()
                    }
                }
            }

            override fun onFailure(call: Call<WorldwideResponse>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }

    private fun fetchWorldwideHistory(lastdays: String): LiveData<HistoricalWorldwideResponse> {
        CovidService.getApi().getWorldwideHistory(lastdays)
            .enqueue(object : Callback<HistoricalWorldwideResponse> {
                override fun onResponse(
                    call: Call<HistoricalWorldwideResponse>,
                    response: Response<HistoricalWorldwideResponse>
                ) {
                    Timber.d("onResponse: ")
                    response.let {
                        if (it.isSuccessful) {
                            historicalWorldwideResponse.value = it.body()
                        }
                    }
                }

                override fun onFailure(call: Call<HistoricalWorldwideResponse>, t: Throwable) {
                    Timber.d("onFailure: " + t.message)
                    errorMessage.value = t.message
                }
            })
        return historicalWorldwideResponse
    }

    private fun fetchVietnamData() {
        CovidService.getApi().getVietnamData().enqueue(object : Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>,
                response: Response<CountryResponse>
            ) {
                if (response.isSuccessful) {
                    vietnamResponse.value = response.body()
                }
            }

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }

    private fun fetchVietnamHistory(lastdays: String): LiveData<HistoricalCountryResponse> {
        CovidService.getApi().getCountryHistory("vietnam", lastdays)
            .enqueue(object : Callback<HistoricalCountryResponse> {
                override fun onResponse(
                    call: Call<HistoricalCountryResponse>,
                    response: Response<HistoricalCountryResponse>
                ) {
                    Timber.d("onResponse: ")
                    response.let {
                        if (it.isSuccessful) {
                            historicalVietnamResponse.value = it.body()
                        }
                    }
                }

                override fun onFailure(call: Call<HistoricalCountryResponse>, t: Throwable) {
                    Timber.d("onFailure: %s", t.message)
                    errorMessage.value = t.message
                }
            })
        return historicalVietnamResponse
    }

    fun fetchCountryList(): LiveData<ArrayList<CountryResponse>> {
        CovidService.getApi().getCountryListData()
            .enqueue(object : Callback<ArrayList<CountryResponse>> {
                override fun onResponse(
                    call: Call<ArrayList<CountryResponse>>,
                    response: Response<ArrayList<CountryResponse>>
                ) {
                    if (response.isSuccessful) {
                        countryList.value = response.body()
                    }
                }

                override fun onFailure(call: Call<ArrayList<CountryResponse>>, t: Throwable) {
                    errorMessage.value = t.message
                }
            })
        return countryList
    }
}