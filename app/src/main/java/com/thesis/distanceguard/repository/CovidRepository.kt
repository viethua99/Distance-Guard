package com.thesis.distanceguard.repository

import androidx.annotation.WorkerThread
import com.thesis.distanceguard.mapper.CountryMapper
import com.thesis.distanceguard.mapper.HistoricalMapper
import com.thesis.distanceguard.mapper.WorldwideMapper
import com.thesis.distanceguard.retrofit.CovidApi
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.retrofit.response.WorldwideResponse
import com.thesis.distanceguard.room.CovidDatabase
import com.thesis.distanceguard.room.entities.CountryEntity
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity
import com.thesis.distanceguard.room.entities.HistoricalWorldwideEntity
import com.thesis.distanceguard.room.entities.WorldwideEntity
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Viet Hua on 05/20/2021.
 */
class CovidRepository(private val covidDatabase: CovidDatabase, private val covidApi: CovidApi) {

    // LOCAL
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    private suspend fun updateLocalWorldwideData(worldwideResponse: WorldwideResponse?) {
        Timber.d("updateLocalData")
        worldwideResponse?.let {
            covidDatabase.worldwideDao().deleteAll()
            covidDatabase.worldwideDao().insert(WorldwideMapper.responseToEntity(worldwideResponse))
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    private suspend fun updateHistoricalCountry(historicalCountryResponse: HistoricalCountryResponse?) {
        historicalCountryResponse?.let {
            Timber.d("updateHistoricalCountry: $it")

//            covidDatabase.historicalDao().deleteAll()
            covidDatabase.historicalDao().insert(HistoricalMapper.responseToEntity(it))
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    private suspend fun updateLocalCountryList(countryList: ArrayList<CountryResponse>?) {
        Timber.d("updateLocalData: $countryList")
        countryList?.let {
            covidDatabase.countryDao().deleteAll()
            covidDatabase.countryDao().insert(CountryMapper.responseListToEntities(it))
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLocalCountryList(): ArrayList<CountryEntity> {
        return ArrayList(covidDatabase.countryDao().getAllCountryData())
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLocalWorldwideData(): WorldwideEntity {
        return covidDatabase.worldwideDao().getWorldwide()
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLocalHistoricalCountry(countryName:String): HistoricalCountryEntity {
        return covidDatabase.historicalDao().getHistoricalCountryEntity(countryName)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCountryData(countryName: String): CountryEntity {
        return covidDatabase.countryDao().getCountry(countryName)
    }

    //REMOTE
    suspend fun getWorldwideData(): Result<WorldwideEntity> {
        return try {
            val response = covidApi.getWorldwideData()
            if (response.isSuccessful) {
                updateLocalWorldwideData(response.body())
                val entity = getLocalWorldwideData()
                Success(entity)
            } else {
                Error(Exception("Something went wrong"))
            }
        } catch (exception: Exception) {
            Error(exception)
        }
    }


    suspend fun getVietnamData(): CountryEntity {
        return getCountryData("Vietnam")
    }

    suspend fun getCountryListData(): Result<ArrayList<CountryEntity>> {
        return try {
            val response = covidApi.getCountryListData()
            if (response.isSuccessful) {
                updateLocalCountryList(response.body())
                val data = getLocalCountryList()
                Success(data)
            } else {
                Error(Exception("Something went wrong"))
            }
        } catch (exception: Exception) {
            Error(exception)
        }

    }

    suspend fun getWorldwideHistory(lastdays: String): Result<HistoricalWorldwideEntity> {
        return try {
            val response = covidApi.getWorldwideHistory(lastdays)
            if (response.isSuccessful) {
                Success(HistoricalWorldwideEntity())
            } else {
                Error(Exception("Something went wrong"))
            }
        } catch (exception: Exception) {
            Error(exception)
        }

    }

    suspend fun getCountryHistory(
        country: String,
        lastdays: String
    ): Result<HistoricalCountryEntity> {
        return try {
            val response = covidApi.getCountryHistory(country, lastdays)
            if (response.isSuccessful) {
                updateHistoricalCountry(response.body())
                val entity = getLocalHistoricalCountry(country)
                Success(entity)
            } else {
                Error(Exception("Something went wrong"))
            }
        } catch (exception: Exception) {
            Error(exception)
        }

    }
}