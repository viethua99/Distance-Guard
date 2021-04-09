package com.thesis.distanceguard.di

import com.thesis.distanceguard.presentation.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(includes = [ViewModelModule::class])
abstract class AppBindingModule {
    @ContributesAndroidInjector
    abstract fun mainActivity() : MainActivity

}