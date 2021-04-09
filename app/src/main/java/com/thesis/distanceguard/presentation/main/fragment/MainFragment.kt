package com.thesis.distanceguard.presentation.main.fragment

import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment

class MainFragment : BaseFragment() {
    companion object {
        const val TAG = "MainFragment"
    }
    override fun getResLayoutId(): Int {
       return R.layout.fragment_main
    }

    override fun onMyViewCreated(view: View) {

    }
}