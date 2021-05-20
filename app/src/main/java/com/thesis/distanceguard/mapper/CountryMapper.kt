package com.thesis.distanceguard.mapper

import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.room.entities.CountryEntity
import com.thesis.distanceguard.room.entities.CountryInfoEntity

object CountryMapper {

    private fun responseToEntity(countryResponse: CountryResponse) : CountryEntity {
        return CountryEntity(
             country = countryResponse.country,
             active = countryResponse.active,
             activePerOneMillion = countryResponse.activePerOneMillion,
             cases = countryResponse.cases,
             casesPerOneMillion = countryResponse.casesPerOneMillion,
             continent = countryResponse.continent,
             countryInfoEntity = CountryInfoEntity(
                  id = countryResponse.countryInfo.id,
                  flag  = countryResponse.countryInfo.flag,
                  iso2  = countryResponse.countryInfo.iso2,
                  iso3  = countryResponse.countryInfo.iso3,
                  lat  = countryResponse.countryInfo.lat,
                  long  = countryResponse.countryInfo.long
             ),
             critical = countryResponse.critical,
             criticalPerOneMillion = countryResponse.criticalPerOneMillion,
             deaths = countryResponse.deaths,
             deathsPerOneMillion = countryResponse.deathsPerOneMillion,
             oneCasePerPeople = countryResponse.oneCasePerPeople,
             oneDeathPerPeople = countryResponse.oneDeathPerPeople,
             oneTestPerPeople = countryResponse.oneTestPerPeople,
             population = countryResponse.population,
             recovered = countryResponse.recovered,
             recoveredPerOneMillion = countryResponse.recoveredPerOneMillion,
             tests = countryResponse.tests,
             testsPerOneMillion = countryResponse.testsPerOneMillion,
             todayCases = countryResponse.todayCases,
             todayDeaths = countryResponse.todayDeaths,
             todayRecovered = countryResponse.todayRecovered,
             updated = countryResponse.updated
        )
    }

    fun responseListToEntities(countryList: ArrayList<CountryResponse>) : ArrayList<CountryEntity>{
        val dataList = ArrayList<CountryEntity>()
        for(country in countryList){
            val entity = responseToEntity(country)
            dataList.add(entity)
        }
        return dataList
    }

}