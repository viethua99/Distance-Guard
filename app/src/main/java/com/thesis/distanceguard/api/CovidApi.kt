package com.thesis.distanceguard.api

import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalWorldwideResponse
import com.thesis.distanceguard.api.model.HistoricalVietnamResponse
import com.thesis.distanceguard.api.model.WorldwideResponse
import retrofit2.Call
import retrofit2.http.GET

interface CovidApi {
    @GET("/v3/covid-19/all")
    fun getWorldwideData(): Call<WorldwideResponse>

    @GET("/v3/covid-19/countries?sort=cases")
    fun getCountryListData(): Call<ArrayList<CountryResponse>>

    @GET("/v3/covid-19/historical/all")
    fun getWorldwideHistory(): Call<HistoricalWorldwideResponse>

    @GET("/v3/covid-19/countries/Vietnam?strict=true")
    fun getVietnamData(): Call<CountryResponse>

    @GET("/v3/covid-19/historical/VietNam?lastdays=30")
    fun getVietnamHistory(): Call<HistoricalVietnamResponse>
}