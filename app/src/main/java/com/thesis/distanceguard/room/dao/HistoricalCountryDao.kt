package com.thesis.distanceguard.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thesis.distanceguard.room.entities.HistoricalCountryEntity

/**
 * Created by Viet Hua on 05/21/2021.
 */

@Dao
interface HistoricalCountryDao {

    @Query("SELECT * FROM historical_country WHERE country=:countryName")
    suspend fun getHistoricalCountryEntity(countryName:String): HistoricalCountryEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(historicalCountryEntity: HistoricalCountryEntity)

    @Query("DELETE FROM historical_country")
    suspend fun deleteAll()


}