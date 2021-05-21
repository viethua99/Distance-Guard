package com.thesis.distanceguard.mapper

import com.thesis.distanceguard.retrofit.response.HistoricalCountryResponse
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity
import com.thesis.distanceguard.room.entities.HistoricalWorldwideEntity

/**
 * Created by Viet Hua on 05/21/2021.
 */
object HistoricalMapper {

    fun responseToEntity(historicalCountryResponse: HistoricalCountryResponse) : HistoricalCountryEntity {
        return HistoricalCountryEntity(
             country = historicalCountryResponse.country,
             province = ArrayList(historicalCountryResponse.province),
             timeline = HistoricalWorldwideEntity(
                  cases = HashMap(historicalCountryResponse.timeline.cases),
                  deaths = HashMap(historicalCountryResponse.timeline.deaths),
                  recovered = HashMap(historicalCountryResponse.timeline.recovered)
             )
        )
    }

}