package com.thesis.distanceguard.presentation.map

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.repository.Error
import com.thesis.distanceguard.repository.Success
import com.thesis.distanceguard.room.entities.CountryEntity
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class MapViewModel @Inject constructor(private val covidRepository: CovidRepository) : ViewModel() {
     val countryList = MutableLiveData<ArrayList<CountryEntity>>()
    val nearbyCountryList = MutableLiveData<ArrayList<CountryEntity>?>()

    val errorMessage = MutableLiveData<String>()

    fun fetchCountryList(): LiveData<ArrayList<CountryEntity>> {
        viewModelScope.launch {
            when (val result = covidRepository.getCountryListData()) {
                is Success<ArrayList<CountryEntity>> -> {
                    countryList.value = result.data
                }
                is Error -> {
                    countryList.value = covidRepository.getLocalCountryList()
                    errorMessage.value = result.message
                }

            }
        }
        return countryList
    }

    fun checkNearbyCountryList(currentLocation:Location){
        val list = ArrayList<CountryEntity>()
        countryList.value?.let {
            for (country in it) {
                if (checkIfCountryIsNearby(country,currentLocation)) {
                    list.add(country)
                }
            }
            nearbyCountryList.postValue(list)
        }
    }

    private fun checkIfCountryIsNearby(countryEntity: CountryEntity?,currentLocation: Location): Boolean {
        Timber.d("checkIfSportFieldIsNearby")
        countryEntity?.let {
            val target = Location("target")
            target.latitude = it.countryInfoEntity!!.latitude!!
            target.longitude = it.countryInfoEntity!!.longitude!!
            return currentLocation.distanceTo(target) < 1000000
        }
        return false
    }

}