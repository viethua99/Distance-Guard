package com.thesis.distanceguard.di

import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.factory.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

}