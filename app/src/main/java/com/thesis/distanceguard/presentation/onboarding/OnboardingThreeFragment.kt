package com.thesis.distanceguard.presentation.onboarding

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import com.thesis.distanceguard.R
import com.thesis.distanceguard.ble_module.BLEController
import com.thesis.distanceguard.databinding.FragmentOnboardingThreeBinding
import com.thesis.distanceguard.myapp.Constants
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import com.thesis.distanceguard.presentation.main.fragment.MainFragment

class OnboardingThreeFragment : BaseFragment<FragmentOnboardingThreeBinding>(){
    override fun getResLayoutId(): Int {
        return R.layout.fragment_onboarding_three
    }

    override fun onMyViewCreated(view: View) {
        binding.btnStart.setOnClickListener {
            boardingCompleted()
            (activity as MainActivity).replaceFragmentWithoutAddToBackStack(MainFragment(), MainFragment.TAG, R.id.container_main)
        }
    }

    private fun boardingCompleted(){
        BLEController.uuidString = BLEController.getNewUniqueId() // Create a new UUID for whole app
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