package com.thesis.distanceguard.presentation.main

import android.os.Bundle
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseActivity
import com.thesis.distanceguard.presentation.main.fragment.MainFragment
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun getResLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setupViews()
    }

    fun setToolbarTitle(title:String){
        tv_toolbar_title.text = title
    }

    private fun setupViews(){
        Timber.d("setupViews")
        replaceFragmentWithoutAddToBackStack(MainFragment(), MainFragment.TAG, R.id.container_main)
    }
}