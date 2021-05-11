package com.thesis.distanceguard.api

import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.HistoricalWorldwideResponse
import com.thesis.distanceguard.api.model.HistoricalCountryResponse
import com.thesis.distanceguard.api.model.WorldwideResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CovidApi {
    @GET("/v3/covid-19/all")
    fun getWorldwideData(): Call<WorldwideResponse>

    @GET("/v3/covid-19/countries?sort=todayCases")
    fun getCountryListData(): Call<ArrayList<CountryResponse>>

    @GET("/v3/covid-19/historical/all")
    fun getWorldwideHistory(@Query("lastdays") lastdays:String): Call<HistoricalWorldwideResponse>

    @GET("/v3/covid-19/countries/Vietnam?strict=true")
    fun getVietnamData(): Call<CountryResponse>

    @GET("/v3/covid-19/historical/{country}")
    fun getCountryHistory(@Path("country") country: String, @Query("lastdays") lastdays: String): Call<HistoricalCountryResponse>
}