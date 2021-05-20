package com.thesis.distanceguard.room

import androidx.room.*
import com.thesis.distanceguard.room.entities.WorldwideEntity

/**
 * Created by Viet Hua on 05/20/2021.
 */

@Dao
interface WorldwideDao {

    @Insert
    suspend fun insert(worldwide : WorldwideEntity)

    @Query("SELECT * FROM WorldwideEntity")
    suspend fun getWorldwide(): WorldwideEntity


    @Query("DELETE FROM WorldwideEntity")
    suspend fun deleteAll()

}