package com.thesis.distanceguard.api

data class World(
    var updated: Long,
    var cases: Long,
    var todayCases: Long,
    var deaths: Long,
    var todayDeaths: Long,
    var recovered: Long,
    var todayRecovered: Int,
    var active: Long,
    var critical: Int,
    var casesPerOneMillion: Double,
    var deathsPerOneMillion: Double,
    var tests: Long,
    var testsPerOneMillion: Double,
    var population: Long,
    var oneCasePerPeople: Double,
    var oneDeathPerPeople: Double,
    var oneTestPerPeople: Double,
    var undefined: Int,
    var activePerOneMillion: Double,
    var recoveredPerOneMillionval: Double,
    var criticalPerOneMillion: Double,
    var affectedCountries: Int
)

