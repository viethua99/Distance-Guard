package com.thesis.distanceguard.room

import androidx.room.*

/**
 * Created by Viet Hua on 05/20/2021.
 */

@Dao
interface CovidDao {
    @Query("SELECT * FROM students ORDER BY name ASC")
    suspend fun getStudents(): List<Student>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(student: Student)

    @Query("DELETE FROM students")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(student: Student)

    @Update
    suspend fun update(student: Student)
}