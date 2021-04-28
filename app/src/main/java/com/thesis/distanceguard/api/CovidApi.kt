package com.thesis.distanceguard.api

import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalAllResponse
import com.thesis.distanceguard.api.model.TotalResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CovidApi {
    @GET("/v3/covid-19/all")
    fun getAll(): Call<TotalResponse>

    @GET("/v3/covid-19/countries?sort=cases")
    fun getCountries(): Call<ArrayList<CountryResponse>>

    @GET("/v3/covid-19/historical/all")
    fun getHistoricalAll(): Call<HistoricalAllResponse>

    @GET("/v3/covid-19/countries/Vietnam?strict=true")
    fun getVietNam(): Call<CountryResponse>

    @GET("/v3/covid-19/historical/VietNam?lastdays=30")
    fun getHistoryVietNam(): Call<HistoricalAllResponse>
}