package com.thesis.distanceguard.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.retrofit.CovidService
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import retrofit2.Call
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class DetailViewModel @Inject constructor() : ViewModel() {
    private val dataCountryResponse = MutableLiveData<HistoricalCountryResponse>()
    val errorMessage = MutableLiveData<String>()

    fun fetchCountry(countryID: String): LiveData<HistoricalCountryResponse> {
//        CovidService.getApi().getCountryHistory(countryID,"30")
//            .enqueue(object : Callback<HistoricalCountryResponse> {
//                override fun onResponse(
//                    call: Call<HistoricalCountryResponse>,
//                    response: Response<HistoricalCountryResponse>
//                ) {
//                    Timber.d("check value " + response.body())
//                    if (response.isSuccessful) {
//                        dataCountryResponse.value = response.body()
//                    } else {
//                        errorMessage.value = "Cannot fetch data from server"                    }
//                }
//
//                override fun onFailure(call: Call<HistoricalCountryResponse>, t: Throwable) {
//                    errorMessage.value = t.message
//                }
//            })
        return dataCountryResponse
    }

}