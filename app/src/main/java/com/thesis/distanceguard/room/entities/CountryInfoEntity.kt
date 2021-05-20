package com.thesis.distanceguard.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity
data class CountryInfoEntity(
    @PrimaryKey
    @field:Json(name = "_id")
    var id: Int? = null,

    @field:Json(name = "flag")
    var flag: String? = null,

    @field:Json(name = "iso2")
    var iso2: String? = null,

    @field:Json(name = "iso3")
    var iso3: String? = null,

    @field:Json(name = "latitude")
    var latitude: Double? = null,

    @field:Json(name = "longitude")
    var longitude: Double? = null
)