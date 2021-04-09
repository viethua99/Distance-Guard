package com.thesis.distanceguard.presentation.main

import android.os.Bundle
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseActivity
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun getResLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setupViews()
    }

    private fun setupViews(){
        Timber.d("setupViews")
    }
}