package com.thesis.distanceguard.presentation.information

import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class InformationFragment : BaseFragment(){

    companion object {
        const val TAG = "InformationFragment"
    }


    override fun getResLayoutId(): Int {
        return R.layout.fragment_information
    }

    override fun onMyViewCreated(view: View) {
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        val mainActivity = activity as MainActivity
        mainActivity.appBarLayout.visibility = View.VISIBLE
    }
}