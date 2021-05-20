package com.thesis.distanceguard.di

import android.content.Context
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.myapp.MyApplication
import com.thesis.distanceguard.repository.CovidRepository
import com.thesis.distanceguard.retrofit.CovidService
import com.thesis.distanceguard.room.CovidDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(myApplication: MyApplication): Context {
        return myApplication
    }

    @Provides
    @Singleton
    fun provideBLEController(): BLEController {
        return BLEController
    }

    @Provides
    @Singleton
    fun provideCovidDatabase(context: Context): CovidDatabase {
        return CovidDatabase.getDatabase(context)
    }


    @Provides
    @Singleton
    fun provideCovidRepository(covidDatabase: CovidDatabase): CovidRepository{
        return CovidRepository(covidDatabase,CovidService.getApi())
    }

}