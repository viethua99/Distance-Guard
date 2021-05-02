package com.thesis.distanceguard.di

import com.thesis.distanceguard.presentation.camera.CameraActivity
import com.thesis.distanceguard.presentation.countries.CountriesFragment
import com.thesis.distanceguard.presentation.dashboard.DashboardFragment
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.presentation.scanner.ScannerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
abstract class AppBindingModule {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun cameraActivity(): CameraActivity

    @ContributesAndroidInjector
    abstract fun scannerFragment(): ScannerFragment

    @ContributesAndroidInjector
    abstract fun dashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun countriesFragment(): CountriesFragment

    @ContributesAndroidInjector
    abstract fun detailFragment(): DetailFragment

    @ContributesAndroidInjector
    abstract fun detailFragment(): DetailFragment

}