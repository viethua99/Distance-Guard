package com.thesis.distanceguard.api

import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.api.model.TotalResponse
import retrofit2.Call
import retrofit2.http.GET

interface CovidApi {
    @GET("/v3/covid-19/all")
    fun getAll(): Call<TotalResponse>

    @GET("/v3/covid-19/countries?sort=cases")
    fun getCountries(): Call<ArrayList<CountryResponse>>
}