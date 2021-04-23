package com.thesis.distanceguard.presentation.detail

import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.MainActivity

class DetailFragment : BaseFragment(){
    companion object {
        const val TAG = "DetailFragment"
    }


    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setupToolbarTitle("France")
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        setupToolbarTitle("Countries")
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

    }
}