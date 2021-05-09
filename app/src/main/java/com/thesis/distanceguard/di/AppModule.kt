package com.thesis.distanceguard.di

import android.content.Context
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.myapp.MyApplication
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

}