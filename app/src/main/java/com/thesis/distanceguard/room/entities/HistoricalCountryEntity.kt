package com.thesis.distanceguard.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.squareup.moshi.Json
import com.thesis.distanceguard.retrofit.response.HistoricalWorldwideResponse
import com.thesis.distanceguard.room.converter.ListTypeConverter
import com.thesis.distanceguard.room.converter.MapTypeConverter

/**
 * Created by Viet Hua on 05/21/2021.
 */

@Entity(tableName = "historical_country")
data class HistoricalCountryEntity(
    @PrimaryKey
    @field:Json(name = "country")
    var country: String,

    @TypeConverters(ListTypeConverter::class)
    @field:Json(name = "province")
    var province: ArrayList<String>? = null,

    @Embedded
    @field:Json(name = "timeline")
    var timeline: HistoricalWorldwideEntity? = null
)