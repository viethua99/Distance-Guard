package com.thesis.distanceguard.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.distanceguard.retrofit.CovidService
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.retrofit.response.WorldwideResponse
import com.thesis.distanceguard.model.ChartDate
import com.thesis.distanceguard.model.ChartType
import com.thesis.distanceguard.model.DashboardMode
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.repository.Success
import com.thesis.distanceguard.room.Student
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel @Inject constructor(private val covidRepository: CovidRepository) :
    ViewModel() {
    val chartType = MutableLiveData<ChartType>(ChartType.DAILY)
    private val chartDate = MutableLiveData<ChartDate>(ChartDate.THIRTY_DAYS)
    private val dashboardMode = MutableLiveData<DashboardMode>(DashboardMode.WORLDWIDE)


    val worldwideResponse = MutableLiveData<WorldwideResponse>()
    val vietnamResponse = MutableLiveData<CountryResponse>()

    val historicalWorldwideResponse = MutableLiveData<HistoricalWorldwideResponse>()
    val historicalVietnamResponse = MutableLiveData<HistoricalCountryResponse>()

    val errorMessage = MutableLiveData<String>()
    val countryList = MutableLiveData<ArrayList<CountryResponse>>()

//    fun testDataBase() {
//        viewModelScope.launch {
//            covidRepository.insert(Student(name = "hello", email = "123@gmail.com", sdt = ""))
//            covidRepository.insert(Student(name = "hello2", email = "12344@gmail.com", sdt = ""))
//            covidRepository.insert(Student(name = "hello3", email = "12344@gmail.com", sdt = ""))
//
//        }
//    }

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
//                viewModelScope.launch {
//                    val list = covidRepository.getAllStudents()
//                    for (student in list) {
//                        Timber.d(student.toString())
//                    }
//                }
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
                is Success<WorldwideResponse> -> {
                    worldwideResponse.value = result.data
                }
                is Error -> {
                    errorMessage.value = result.message
                }

            }
        }
    }

    private fun fetchWorldwideHistory(lastdays: String) {
        viewModelScope.launch {
            when (val result = covidRepository.getWorldwideHistory(lastdays)) {
                is Success<HistoricalWorldwideResponse> -> {
                    historicalWorldwideResponse.value = result.data
                }
                is Error -> {
                    errorMessage.value = result.message
                }

            }
        }
    }

    private fun fetchVietnamData() {
        viewModelScope.launch {
            when (val result = covidRepository.getVietnamData()) {
                is Success<CountryResponse> -> {
                    vietnamResponse.value = result.data
                }
                is Error -> {
                    errorMessage.value = result.message
                }

            }
        }
    }

    private fun fetchVietnamHistory(lastdays: String) {
        viewModelScope.launch {
            when (val result = covidRepository.getCountryHistory("vietnam",lastdays)) {
                is Success<HistoricalCountryResponse> -> {
                    historicalVietnamResponse.value = result.data
                }
                is Error -> {
                    errorMessage.value = result.message
                }

            }
        }
    }

    fun fetchCountryList(): LiveData<ArrayList<CountryResponse>> {
        viewModelScope.launch {
            when (val result = covidRepository.getCountryListData()) {
                is Success<ArrayList<CountryResponse>> -> {
                    countryList.value = result.data
                }
                is Error -> {
                    errorMessage.value = result.message
                }

            }
        }
        return countryList
    }
}