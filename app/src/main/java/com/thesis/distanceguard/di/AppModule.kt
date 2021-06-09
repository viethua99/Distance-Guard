package com.thesis.distanceguard.di

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.IntentFilter
import android.location.LocationManager
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.ble_module.state.BluetoothStateReceiver
import com.thesis.distanceguard.ble_module.state.LocationStateReceiver
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
    fun provideBluetoothStateReceiver(context: Context): BluetoothStateReceiver {
        val bluetoothStateReceiver = BluetoothStateReceiver()
        val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothStateReceiver, intentFilter)
        return bluetoothStateReceiver
    }

    @Provides
    @Singleton
    fun provideLocationStateReceiver(context: Context): LocationStateReceiver {
        val locationStateReceiver = LocationStateReceiver()
        val intentFilter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        context.registerReceiver(locationStateReceiver, intentFilter)
        return locationStateReceiver
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