package com.thesis.distanceguard.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.model.HistoricalVietnamResponse
import retrofit2.Call
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class DetailViewModel @Inject constructor() : ViewModel() {
    private val dataCountryResponse = MutableLiveData<HistoricalVietnamResponse>()

    fun fetchCountry(countryID: String): LiveData<HistoricalVietnamResponse> {
        CovidService.getApi().getCountryHistory(countryID)
            .enqueue(object : Callback<HistoricalVietnamResponse> {
                override fun onResponse(
                    call: Call<HistoricalVietnamResponse>,
                    response: Response<HistoricalVietnamResponse>
                ) {
                    Timber.d("check value " + response.body())
                    if (response.isSuccessful) {
                        dataCountryResponse.value = response.body()
                    }
                }

                override fun onFailure(call: Call<HistoricalVietnamResponse>, t: Throwable) {
                    dataCountryResponse.value = null
                }
            })
        return dataCountryResponse
    }

}