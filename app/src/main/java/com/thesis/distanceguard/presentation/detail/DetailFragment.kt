package com.thesis.distanceguard.presentation.detail

import android.view.MenuItem
import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity

class DetailFragment : BaseFragment() {
    companion object {
        const val TAG = "DetailFragment"
    }


    override fun getResLayoutId(): Int {
        return R.layout.fragment_detail
    }

    override fun onMyViewCreated(view: View) {
        setHasOptionsMenu(true)
        setupToolbarTitle("France")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStop() {
        super.onStop()
        setupToolbarTitle("Countries")
        val mainActivity = activity as MainActivity
        mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(false)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity!!.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}