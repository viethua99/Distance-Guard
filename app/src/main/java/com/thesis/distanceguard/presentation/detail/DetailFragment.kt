package com.thesis.distanceguard.presentation.detail

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import timber.log.Timber

class DetailFragment : BaseFragment() {
    companion object {
        const val TAG = "DetailFragment"
    }


    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)
    }


    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        setupToolbarTitle("France")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
        setupToolbarTitle("Countries")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }



}