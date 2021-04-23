package com.thesis.distanceguard.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.presentation.camera.CameraViewModel
import com.thesis.distanceguard.factory.ViewModelFactory
import com.thesis.distanceguard.factory.ViewModelKey
import com.thesis.distanceguard.presentation.main.activity.MainActivityViewModel
import com.thesis.distanceguard.presentation.scanner.ScannerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityModel(mainActivityViewModel: MainActivityViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(CameraViewModel::class)
    abstract fun bindCameraViewModel(cameraViewModel: CameraViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ScannerViewModel::class)
    abstract fun bindScannerViewModel(scannerViewModel: ScannerViewModel): ViewModel
}