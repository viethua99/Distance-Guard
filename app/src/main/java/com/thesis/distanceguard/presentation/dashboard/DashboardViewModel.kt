package com.thesis.distanceguard.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalWorldwideResponse
import com.thesis.distanceguard.api.model.HistoricalVietnamResponse
import com.thesis.distanceguard.api.model.WorldwideResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class DashboardViewModel @Inject constructor() : ViewModel() {
    private val worldwideResponse = MutableLiveData<WorldwideResponse>()
    private val historicalWorldwideResponse = MutableLiveData<HistoricalWorldwideResponse>()
    private val vietnamResponse = MutableLiveData<CountryResponse>()
    private val historicalVietnamResponse = MutableLiveData<HistoricalVietnamResponse>()
    val countryList = MutableLiveData<ArrayList<CountryResponse>>()


    fun fetchWorldwideData(): LiveData<WorldwideResponse> {
        CovidService.getApi().getWorldwideData().enqueue(object : Callback<WorldwideResponse> {
            override fun onResponse(call: Call<WorldwideResponse>, response: Response<WorldwideResponse>) {
                response.let {
                    if (it.isSuccessful) {
                        worldwideResponse.value = it.body()
                    }

                }
            }

            override fun onFailure(call: Call<WorldwideResponse>, t: Throwable) {
                worldwideResponse.value = null
            }
        })
        return worldwideResponse
    }

    fun fetchWorldwideHistory(): LiveData<HistoricalWorldwideResponse> {
        CovidService.getApi().getWorldwideHistory().enqueue(object : Callback<HistoricalWorldwideResponse> {
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
                historicalWorldwideResponse.value = null
            }
        })
        return historicalWorldwideResponse
    }

    fun fetchVietnamData(): LiveData<CountryResponse> {
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
                vietnamResponse.value = null
            }
        })
        return vietnamResponse
    }

    fun fetchVietnamHistory(): LiveData<HistoricalVietnamResponse> {
        CovidService.getApi().getVietnamHistory()
            .enqueue(object : Callback<HistoricalVietnamResponse> {
                override fun onResponse(
                    call: Call<HistoricalVietnamResponse>,
                    response: Response<HistoricalVietnamResponse>
                ) {
                    Timber.d("onResponse: ")
                    response.let {
                        if (it.isSuccessful) {
                            historicalVietnamResponse.value = it.body()
                        }
                    }
                }

                override fun onFailure(call: Call<HistoricalVietnamResponse>, t: Throwable) {
                    Timber.d("onFailure: " + t.message)
                    historicalVietnamResponse.value = null
                }
            })
        return historicalVietnamResponse
    }

    fun fetchCountryList(): LiveData<ArrayList<CountryResponse>>{
        CovidService.getApi().getCountryListData().enqueue(object : Callback<ArrayList<CountryResponse>>{
            override fun onResponse(
                call: Call<ArrayList<CountryResponse>>,
                response: Response<ArrayList<CountryResponse>>
            ) {
                if(response.isSuccessful){
                    countryList.value = response.body()
                }
            }

            override fun onFailure(call: Call<ArrayList<CountryResponse>>, t: Throwable) {
                countryList.value = null
            }
        })
        return countryList
    }
}