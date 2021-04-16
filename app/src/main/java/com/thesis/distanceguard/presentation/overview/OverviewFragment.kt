package com.thesis.distanceguard.presentation.overview

import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.MainActivity
import com.thesis.distanceguard.presentation.map.MapFragment
import kotlinx.android.synthetic.main.fragment_overview.*

class OverviewFragment : BaseFragment() {
    override fun getResLayoutId(): Int {
        return R.layout.fragment_overview
    }

    override fun onMyViewCreated(view: View) {
        fab_map.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.replaceFragment(MapFragment(),"Map Fragment",R.id.container_main)
        }
    }
}