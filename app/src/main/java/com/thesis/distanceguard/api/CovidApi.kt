package com.thesis.distanceguard.api

import retrofit2.Call
import retrofit2.http.GET

interface CovidApi {
    @GET("/v3/covid-19/all")
    fun getAll(): Call<TotalResponse>
}