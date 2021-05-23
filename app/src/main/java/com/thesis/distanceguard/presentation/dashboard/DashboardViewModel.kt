package com.thesis.distanceguard.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.retrofit.response.WorldwideResponse
import com.thesis.distanceguard.model.ChartDate
import com.thesis.distanceguard.model.ChartType
import com.thesis.distanceguard.model.DashboardMode
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.repository.Result

import com.thesis.distanceguard.repository.Success
import com.thesis.distanceguard.repository.Error
import com.thesis.distanceguard.room.entities.CountryEntity
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity
import com.thesis.distanceguard.room.entities.HistoricalWorldwideEntity
import com.thesis.distanceguard.room.entities.WorldwideEntity
import kotlinx.coroutines.launch
import timber.log.Timber

import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val covidRepository: CovidRepository) :
    ViewModel() {
    val chartType = MutableLiveData<ChartType>(ChartType.DAILY)
    private val chartDate = MutableLiveData<ChartDate>(ChartDate.THIRTY_DAYS)
    private val dashboardMode = MutableLiveData<DashboardMode>(DashboardMode.WORLDWIDE)


    val worldwideResponse = MutableLiveData<WorldwideEntity?>()
    val vietnamResponse = MutableLiveData<CountryEntity?>()

    val historicalWorldwideResponse = MutableLiveData<HistoricalWorldwideEntity?>()
    val historicalVietnamResponse = MutableLiveData<HistoricalCountryEntity?>()

    val errorMessage = MutableLiveData<String>()
    val countryList = MutableLiveData<ArrayList<CountryEntity>>()

    fun reloadData(){
        fetchDashboardData(dashboardMode.value!!)
        fetchCountryList()
    }

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

    fun fetchChartDataByType(chartType: ChartType) {
        this.chartType.value = chartType
        when (chartType) {
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
        viewModelScope.launch {
            when (val result = covidRepository.getWorldwideData()) {
                is Success<WorldwideEntity> -> {
                    worldwideResponse.value = result.data
                }
                is Error -> {
                    worldwideResponse.value = covidRepository.getLocalWorldwideData()
                    errorMessage.value = result.message
                }

            }
        }
    }

    private fun fetchWorldwideHistory(lastdays: String) {
        viewModelScope.launch {
            when (val result = covidRepository.getWorldwideHistory(lastdays)) {
                is Success<HistoricalWorldwideEntity> -> {
                    historicalWorldwideResponse.value = result.data
                }
                is Error -> {
                    historicalWorldwideResponse.value = covidRepository.getLocalHistoricalWorldwide()

                }

            }
        }
    }

    private fun fetchVietnamData() {
        viewModelScope.launch {
            vietnamResponse.value = covidRepository.getVietnamData()
        }
    }

    private fun fetchVietnamHistory(lastdays: String) {
        viewModelScope.launch {
            when (val result = covidRepository.getCountryHistory("Vietnam",lastdays)) {
                is Success<HistoricalCountryEntity> -> {
                   historicalVietnamResponse.value = result.data
                    Timber.d("entity = ${result.data}")
                }
                is Error -> {
                    val entity = covidRepository.getLocalHistoricalCountry("Vietnam")
                    Timber.d("entity = $entity")
                    historicalVietnamResponse.value = entity
                }

            }
        }
    }

    fun fetchCountryList(): LiveData<ArrayList<CountryEntity>> {
        viewModelScope.launch {
            when (val result = covidRepository.getCountryListData()) {
                is Success<ArrayList<CountryEntity>> -> {
                    countryList.value = result.data
                }
                is Error -> {
                    countryList.value = covidRepository.getLocalCountryList()
                }

            }
        }
        return countryList
    }
}