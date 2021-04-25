package com.thesis.distanceguard.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thesis.distanceguard.api.CovidService
import com.thesis.distanceguard.api.model.TotalResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class DashboardViewModel @Inject constructor() : ViewModel() {
    private val totalResponse = MutableLiveData<TotalResponse>()
    fun fetchTotalCases(): LiveData<TotalResponse> {
        CovidService.getApi().getAll().enqueue(object : Callback<TotalResponse> {
            override fun onResponse(call: Call<TotalResponse>, response: Response<TotalResponse>) {
                response.let {
                    if(it.isSuccessful){
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
}