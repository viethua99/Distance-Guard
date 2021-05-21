package com.thesis.distanceguard.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thesis.distanceguard.room.converter.ListTypeConverter
import com.thesis.distanceguard.room.converter.MapTypeConverter
import com.thesis.distanceguard.room.dao.CountryDao
import com.thesis.distanceguard.room.dao.HistoricalCountryDao
import com.thesis.distanceguard.room.dao.HistoricalWorldwideDao
import com.thesis.distanceguard.room.dao.WorldwideDao
import com.thesis.distanceguard.room.entities.*

/**
 * Created by Viet Hua on 05/20/2021.
 */
@Database(
    entities = [WorldwideEntity::class, CountryEntity::class,
        CountryInfoEntity::class, HistoricalCountryEntity::class,
        HistoricalWorldwideEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MapTypeConverter::class, ListTypeConverter::class)
abstract class CovidDatabase : RoomDatabase() {

    abstract fun worldwideDao(): WorldwideDao
    abstract fun countryDao(): CountryDao
    abstract fun historicalCountryDao(): HistoricalCountryDao
    abstract fun historicalWorldwideDao(): HistoricalWorldwideDao


    companion object {

        @Volatile
        private var INSTANCE: CovidDatabase? = null

        fun getDatabase(ctx: Context): CovidDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    CovidDatabase::class.java, "guard_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}