package com.thesis.distanceguard.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.repository.Success
import com.thesis.distanceguard.retrofit.CovidService
import com.thesis.distanceguard.retrofit.response.CountryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class MapViewModel @Inject constructor(private val covidRepository: CovidRepository) : ViewModel() {
     val countryList = MutableLiveData<ArrayList<CountryResponse>>()
    val errorMessage = MutableLiveData<String>()

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