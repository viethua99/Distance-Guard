package com.thesis.distanceguard.myapp

import ai.kun.opentracesdk_fat.BLETrace
import android.app.Application
import com.thesis.distanceguard.di.AppComponent
import com.thesis.distanceguard.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class MyApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if (MyBuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initDagger()

        //Initialize BlE trace library
        BLETrace.init(this)
        BLETrace.uuidString = BLETrace.getNewUniqueId()
        Timber.d("UUID = ${BLETrace.uuidString}")

    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }

    private fun initDagger() {
        Timber.d("initDagger")
        appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent.inject(this)

    }
}