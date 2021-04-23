package com.thesis.distanceguard.presentation.main.activity

import ai.kun.opentracesdk_fat.BLETrace
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseActivity
import com.thesis.distanceguard.presentation.main.fragment.MainFragment
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {
    private lateinit var mainActivityViewModel: MainActivityViewModel


    override fun getResLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setupViewModel()
        setupViews()
    }

    fun setToolbarTitle(title:String){
        tv_toolbar_title.text = title
    }

    private fun setupViewModel() {
        Timber.d("setupViewModel")
        AndroidInjection.inject(this)
        mainActivityViewModel =
            ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)
        Timber.d("UUID = ${BLETrace.uuidString}")

    }

    private fun setupViews(){
        Timber.d("setupViews")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        replaceFragmentWithoutAddToBackStack(MainFragment(), MainFragment.TAG, R.id.container_main)
    }
}