package com.thesis.distanceguard.presentation.countries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.repository.Error
import com.thesis.distanceguard.repository.Success
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.room.entities.CountryEntity
import kotlinx.coroutines.launch
import timber.log.Timber

import javax.inject.Inject

class CountriesViewModel @Inject constructor(private val covidRepository: CovidRepository): ViewModel(){
     val countryList = MutableLiveData<ArrayList<CountryEntity>>()
     val errorMessage = MutableLiveData<String>()


    fun fetchCountryList(): LiveData<ArrayList<CountryEntity>>{
        viewModelScope.launch {
            when (val result = covidRepository.getCountryListData()) {
                is Success<ArrayList<CountryEntity>> -> {
                    Timber.d("${result.data}")
                    countryList.value = result.data
                }
                is Error -> {
                    Timber.d("TEST2 = ${result.message}")
                    countryList.value = covidRepository.getLocalCountryList()
                    errorMessage.value = result.message
                }

            }
        }
        return countryList
    }
}