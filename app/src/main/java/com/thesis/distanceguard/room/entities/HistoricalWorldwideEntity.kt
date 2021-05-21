package com.thesis.distanceguard.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.squareup.moshi.Json
import com.thesis.distanceguard.room.converter.MapTypeConverter

/**
 * Created by Viet Hua on 05/21/2021.
 */

@Entity
data class HistoricalWorldwideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @TypeConverters(MapTypeConverter::class)
    var cases: HashMap<String, Long>? = null,
    @TypeConverters(MapTypeConverter::class)
    var deaths: HashMap<String, Long>? = null,
    @TypeConverters(MapTypeConverter::class)
    var recovered: HashMap<String, Long>? = null
)
