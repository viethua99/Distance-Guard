package com.thesis.distanceguard.retrofit.response

import com.google.gson.annotations.SerializedName

data class WorldwideResponse(
    @SerializedName("updated")
    val updated: Long,
    @SerializedName("cases")
    val cases: Long,
    @SerializedName("todayCases")
    val todayCases: Long,
    @SerializedName("deaths")
    val deaths: Long,
    @SerializedName("todayDeaths")
    val todayDeaths: Long,
    @SerializedName("recovered")
    val recovered: Long,
    @SerializedName("todayRecovered")
    val todayRecovered: Int,
    @SerializedName("active")
    val active: Long,
    @SerializedName("critical")
    val critical: Int,
    @SerializedName("casesPerOneMillion")
    val casesPerOneMillion: Double,
    @SerializedName("deathsPerOneMillion")
    val deathsPerOneMillion: Double,
    @SerializedName("tests")
    val tests: Long,
    @SerializedName("testsPerOneMillion")
    val testsPerOneMillion: Double,
    @SerializedName("population")
    val population: Long,
    @SerializedName("oneCasePerPeople")
    val oneCasePerPeople: Double,
    @SerializedName("oneDeathPerPeople")
    val oneDeathPerPeople: Double,
    @SerializedName("oneTestPerPeople")
    val oneTestPerPeople: Double,
    @SerializedName("undefined")
    val undefined: Int,
    @SerializedName("activePerOneMillion")
    val activePerOneMillion: Double,
    @SerializedName("recoveredPerOneMillionval")
    val recoveredPerOneMillionval: Double,
    @SerializedName("criticalPerOneMillion")
    val criticalPerOneMillion: Double,
    @SerializedName("affectedCountries")
    val affectedCountries: Int
)

