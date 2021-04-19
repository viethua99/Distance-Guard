package com.thesis.distanceguard.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.presentation.camera.CameraViewModel
import com.thesis.distanceguard.factory.ViewModelFactory
import com.thesis.distanceguard.factory.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindWorkflowModel(cameraViewModel: CameraViewModel): ViewModel
}