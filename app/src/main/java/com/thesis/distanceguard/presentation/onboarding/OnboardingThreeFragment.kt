package com.thesis.distanceguard.presentation.onboarding

import ai.kun.opentracesdk_fat.BLETrace
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.myapp.Constants
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.presentation.main.fragment.MainFragment
import kotlinx.android.synthetic.main.fragment_onboarding_three.*

class OnboardingThreeFragment : BaseFragment(){
    override fun getResLayoutId(): Int {
        return R.layout.fragment_onboarding_three
    }

    override fun onMyViewCreated(view: View) {
        btn_start.setOnClickListener {
            boardingCompleted()
            (activity as MainActivity).replaceFragmentWithoutAddToBackStack(MainFragment(), MainFragment.TAG, R.id.container_main)
        }
    }

    private fun boardingCompleted(){
        BLETrace.uuidString = BLETrace.getNewUniqueId() // Create a new UUID for whole app
        context?.let {
            val sharedPrefs = it.applicationContext.getSharedPreferences(
                Constants.FILE_NAME_PREFERENCE, Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = sharedPrefs.edit()
            editor.putBoolean(Constants.IS_ONBOARD_COMPLETED_KEY, true)
            editor.apply()
        }
    }
}