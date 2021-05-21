package com.thesis.distanceguard.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity
data class WorldwideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @field:Json(name = "updated")
    var updated: Long? = null,

    @field:Json(name = "updated")
    var cases: Long?  = null,

    @field:Json(name = "updated")
    var todayCases: Long?  = null,

    @field:Json(name = "updated")
    var deaths: Long? = null,

    @field:Json(name = "updated")
    var todayDeaths: Long? = null,

    @field:Json(name = "updated")
    var recovered: Long? = null,

    @field:Json(name = "updated")
    var todayRecovered: Int? = null,

    @field:Json(name = "updated")
    var active: Long? = null,

    @field:Json(name = "updated")
    var critical: Int? = null,

    @field:Json(name = "updated")
    var casesPerOneMillion: Double? = null,

    @field:Json(name = "updated")
    var deathsPerOneMillion: Double? = null,

    @field:Json(name = "updated")
    var tests: Long? = null,

    @field:Json(name = "updated")
    var testsPerOneMillion: Double? = null,

    @field:Json(name = "updated")
    var population: Long? = null,

    @field:Json(name = "updated")
    var oneCasePerPeople: Double? = null,

    @field:Json(name = "updated")
    var oneDeathPerPeople: Double? = null,

    @field:Json(name = "updated")
    var oneTestPerPeople: Double? = null,

    @field:Json(name = "updated")
    var undefined: Int? = null,

    @field:Json(name = "updated")
    var activePerOneMillion: Double? = null,

    @field:Json(name = "updated")
    var recoveredPerOneMillionval: Double? = null,

    @field:Json(name = "updated")
    var criticalPerOneMillion: Double? = null,

    @field:Json(name = "updated")
    var affectedCountries: Int? = null
)