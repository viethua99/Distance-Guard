package com.thesis.distanceguard.mapper

import com.thesis.distanceguard.retrofit.response.WorldwideResponse
import com.thesis.distanceguard.room.entities.WorldwideEntity

object WorldwideMapper {

    fun responseToEntity(worldwideResponse: WorldwideResponse) : WorldwideEntity {
        return WorldwideEntity(
             updated= worldwideResponse.updated,
             cases = worldwideResponse.cases,
             todayCases = worldwideResponse.todayCases,
             deaths = worldwideResponse.deaths,
             todayDeaths = worldwideResponse.todayDeaths,
             recovered = worldwideResponse.recovered,
             todayRecovered = worldwideResponse.todayRecovered,
             active = worldwideResponse.active,
             critical = worldwideResponse.critical,
             casesPerOneMillion = worldwideResponse.casesPerOneMillion,
             deathsPerOneMillion = worldwideResponse.deathsPerOneMillion,
             tests = worldwideResponse.tests,
             testsPerOneMillion = worldwideResponse.testsPerOneMillion,
             population = worldwideResponse.population,
             oneCasePerPeople = worldwideResponse.oneCasePerPeople,
             oneDeathPerPeople = worldwideResponse.oneDeathPerPeople,
             oneTestPerPeople = worldwideResponse.oneTestPerPeople,
             undefined = worldwideResponse.undefined,
             activePerOneMillion = worldwideResponse.activePerOneMillion,
             recoveredPerOneMillionval = worldwideResponse.recoveredPerOneMillionval,
             criticalPerOneMillion = worldwideResponse.criticalPerOneMillion,
             affectedCountries = worldwideResponse.affectedCountries
        )
    }

}