package com.thesis.distanceguard.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.thesis.distanceguard.room.entities.CountryEntity

@Dao
interface CountryDao {

    @Query("SELECT * FROM CountryEntity")
    suspend fun getAllCountryData(): List<CountryEntity>

    @Insert
    suspend fun insert(list: List<CountryEntity>)

    @Query("DELETE FROM CountryEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM CountryEntity WHERE country=:countryName")
    suspend fun getCountry(countryName: String): CountryEntity
}