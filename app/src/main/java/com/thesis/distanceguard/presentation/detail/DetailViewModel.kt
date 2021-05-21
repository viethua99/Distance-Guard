package com.thesis.distanceguard.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.repository.Error
import com.thesis.distanceguard.repository.Success
import com.thesis.distanceguard.retrofit.CovidService
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity
import kotlinx.coroutines.launch
import retrofit2.Call
import javax.inject.Inject
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class DetailViewModel @Inject constructor(private val covidRepository: CovidRepository) : ViewModel() {
    private val dataCountryResponse = MutableLiveData<HistoricalCountryEntity?>()
    val errorMessage = MutableLiveData<String>()

    fun fetchCountry(countryID: String): LiveData<HistoricalCountryEntity?> {
        viewModelScope.launch {
            when (val result = covidRepository.getCountryHistory(countryID,"30")) {
                is Success<HistoricalCountryEntity> -> {
                    dataCountryResponse.value = result.data
                }
                is Error -> {
                    dataCountryResponse.value = covidRepository.getLocalHistoricalCountry(countryID)
                    errorMessage.value = result.message
                }

            }
        }
        return dataCountryResponse
    }

}