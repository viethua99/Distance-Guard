package com.thesis.distanceguard.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.thesis.distanceguard.room.entities.HistoricalWorldwideEntity

/**
 * Created by Viet Hua on 05/21/2021.
 */

@Dao
interface HistoricalWorldwideDao {

    @Query("SELECT * FROM HistoricalWorldwideEntity")
    suspend fun getHistoricalWorldwideEntity(): HistoricalWorldwideEntity

    @Insert
    suspend fun insert(historicalWorldwideEntity: HistoricalWorldwideEntity)

    @Query("DELETE FROM HistoricalWorldwideEntity")
    suspend fun deleteAll()


}