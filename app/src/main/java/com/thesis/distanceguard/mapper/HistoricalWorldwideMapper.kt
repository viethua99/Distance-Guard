package com.thesis.distanceguard.mapper

import com.thesis.distanceguard.retrofit.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.room.entities.HistoricalWorldwideEntity

/**
 * Created by Viet Hua on 05/21/2021.
 */
object HistoricalWorldwideMapper {

    fun responseToEntity(historicalWorldwideResponse: HistoricalWorldwideResponse) : HistoricalWorldwideEntity {
        return  HistoricalWorldwideEntity(
            cases = HashMap(historicalWorldwideResponse.cases),
            deaths = HashMap(historicalWorldwideResponse.deaths),
            recovered = HashMap(historicalWorldwideResponse.recovered)
        )
    }

}