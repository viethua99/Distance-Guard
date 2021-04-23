package com.thesis.distanceguard.di

import com.thesis.distanceguard.presentation.camera.CameraActivity
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.presentation.scanner.ScannerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
abstract class AppBindingModule {
    @ContributesAndroidInjector
    abstract fun mainActivity() : MainActivity

    @ContributesAndroidInjector
    abstract fun cameraActivity() : CameraActivity

    @ContributesAndroidInjector
    abstract fun scannerFragment() : ScannerFragment

}