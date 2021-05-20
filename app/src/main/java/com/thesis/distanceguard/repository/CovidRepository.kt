package com.thesis.distanceguard.repository

import androidx.annotation.WorkerThread
import com.thesis.distanceguard.retrofit.CovidApi
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.retrofit.response.WorldwideResponse
import com.thesis.distanceguard.room.Student
import com.thesis.distanceguard.room.CovidDao
import timber.log.Timber
import java.lang.Exception

/**
 * Created by Viet Hua on 05/20/2021.
 */
class CovidRepository (private val covidDao: CovidDao, private val covidApi: CovidApi) {

    // LOCAL
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllStudents() : List<Student> {
        return covidDao.getStudents()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(student: Student){
        covidDao.insert(student)
    }

    //REMOTE
    suspend fun getWorldwideData(): Result<WorldwideResponse> {
      val response =  covidApi.getWorldwideData()
        return if(response.isSuccessful){
            Success(response.body()!!)
        } else {
            Error(Exception("Something went wrong"))
        }
    }

    suspend fun getVietnamData(): Result<CountryResponse> {
        val response =  covidApi.getVietnamData()
        return if(response.isSuccessful){
            Success(response.body()!!)
        } else {
            Error(Exception("Something went wrong"))
        }
    }

    suspend fun getCountryListData(): Result<ArrayList<CountryResponse>> {
        val response =  covidApi.getCountryListData()
        return if(response.isSuccessful){
            Success(response.body()!!)
        } else {
            Error(Exception("Something went wrong"))
        }
    }

    suspend fun getWorldwideHistory(lastdays:String): Result<HistoricalWorldwideResponse> {
        val response =  covidApi.getWorldwideHistory(lastdays)
        return if(response.isSuccessful){
            Success(response.body()!!)
        } else {
            Error(Exception("Something went wrong"))
        }
    }

    suspend fun getCountryHistory(country:String,lastdays:String): Result<HistoricalCountryResponse> {
        val response =  covidApi.getCountryHistory(country,lastdays)
        return if(response.isSuccessful){
            Success(response.body()!!)
        } else {
            Error(Exception("Something went wrong"))
        }
    }
}