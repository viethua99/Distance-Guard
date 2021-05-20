package com.thesis.distanceguard.retrofit

import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.retrofit.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.retrofit.response.WorldwideResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CovidApi {
    @GET("/v3/covid-19/all")
    suspend fun getWorldwideData(): Response<WorldwideResponse>

    @GET("/v3/covid-19/countries?sort=todayCases")
    suspend fun getCountryListData(): Response<ArrayList<CountryResponse>>

    @GET("/v3/covid-19/historical/all")
    suspend fun getWorldwideHistory(@Query("lastdays") lastdays: String): Response<HistoricalWorldwideResponse>

    @GET("/v3/covid-19/countries/Vietnam?strict=true")
    suspend fun getVietnamData(): Response<CountryResponse>

    @GET("/v3/covid-19/historical/{country}")
    suspend fun getCountryHistory(
        @Path("country") country: String,
        @Query("lastdays") lastdays: String
    ): Response<HistoricalCountryResponse>
}