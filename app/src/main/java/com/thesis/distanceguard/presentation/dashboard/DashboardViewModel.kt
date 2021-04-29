package com.thesis.distanceguard.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalAllResponse
import com.thesis.distanceguard.api.model.HistoricalVietNamResponse
import com.thesis.distanceguard.api.model.TotalResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class DashboardViewModel @Inject constructor() : ViewModel() {
    private val totalResponse = MutableLiveData<TotalResponse>()
    private val historyRespone = MutableLiveData<HistoricalAllResponse>()
    private val vietNamResponse = MutableLiveData<CountryResponse>()
    private val historyVietNamRespone = MutableLiveData<HistoricalVietNamResponse>()

    private val caseList = MutableLiveData<ArrayList<Pair<String, Float>>>()
    fun fetchTotalCases(): LiveData<TotalResponse> {
        CovidService.getApi().getAll().enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>, response: Response<TotalResponse>) {
                response.let {
                    if (it.isSuccessful) {
                        totalResponse.value = it.body()
                    }

                }
            }

            override fun onFailure(call: Call<TotalResponse>, t: Throwable) {
                totalResponse.value = null
            }
        })
        return totalResponse
    }

    fun fetchHistory(): LiveData<HistoricalAllResponse> {
        CovidService.getApi().getHistoricalAll().enqueue(object : Callback<HistoricalAllResponse> {
            override fun onResponse(
                call: Call<HistoricalAllResponse>,
                response: Response<HistoricalAllResponse>
            ) {
                Timber.d("onResponse: ")
                response.let {
                    if (it.isSuccessful) {
                        historyRespone.value = it.body()
                    }
                }
            }

            override fun onFailure(call: Call<HistoricalAllResponse>, t: Throwable) {
                Timber.d("onFailure: " + t.message)
                historyRespone.value = null
            }
        })
        return historyRespone
    }

    fun fetchVietNam(): LiveData<CountryResponse> {
        CovidService.getApi().getVietNam().enqueue(object : Callback<CountryResponse> {
            override fun onResponse(
                call: Call<CountryResponse>,
                response: Response<CountryResponse>
            ) {
                if (response.isSuccessful) {
                    vietNamResponse.value = response.body()
                }

            }

            override fun onFailure(call: Call<CountryResponse>, t: Throwable) {
                Timber.d("onFailure: " + t.message)
                vietNamResponse.value = null
            }
        })
        return vietNamResponse
    }

    fun fetchHistoryVietNam(): LiveData<HistoricalVietNamResponse> {
        CovidService.getApi().getHistoryVietNam()
            .enqueue(object : Callback<HistoricalVietNamResponse> {
                override fun onResponse(
                    call: Call<HistoricalVietNamResponse>,
                    response: Response<HistoricalVietNamResponse>
                ) {
                    Timber.d("onResponse: ")
                    response.let {
                        if (it.isSuccessful) {
                            historyVietNamRespone.value = it.body()
                        }
                    }
                }

                override fun onFailure(call: Call<HistoricalVietNamResponse>, t: Throwable) {
                    Timber.d("onFailure: " + t.message)
                    historyVietNamRespone.value = null
                }
            })
        return historyVietNamRespone
    }
}