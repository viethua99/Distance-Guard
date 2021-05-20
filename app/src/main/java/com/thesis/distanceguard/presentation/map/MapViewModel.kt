package com.thesis.distanceguard.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.retrofit.CovidService
import com.thesis.distanceguard.retrofit.response.CountryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MapViewModel @Inject constructor() : ViewModel() {
     val countryList = MutableLiveData<ArrayList<CountryResponse>>()

    fun fetchCountryList(): LiveData<ArrayList<CountryResponse>> {
//        CovidService.getApi().getCountryListData().enqueue(object :
//            Callback<ArrayList<CountryResponse>> {
//            override fun onResponse(
//                call: Call<ArrayList<CountryResponse>>,
//                response: Response<ArrayList<CountryResponse>>
//            ) {
//                if (response.isSuccessful) {
//                    countryList.value = response.body()
//                }
//            }
//
//            override fun onFailure(call: Call<ArrayList<CountryResponse>>, t: Throwable) {
//                countryList.value = null
//            }
//        })
        return countryList
    }
}