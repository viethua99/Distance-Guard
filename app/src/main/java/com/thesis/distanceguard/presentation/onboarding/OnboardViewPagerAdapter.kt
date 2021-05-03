package com.thesis.distanceguard.presentation.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class OnboardViewPagerAdapter(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {

    companion object {
        const val ON_BOARDING_ONE_PAGE = 0
        const val ON_BOARDING_TWO_PAGE = 1
        const val ON_BOARDING_THREE_PAGE = 2

        const val MAX_PAGES = 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            ON_BOARDING_ONE_PAGE -> OnboardingOneFragment()
            ON_BOARDING_TWO_PAGE -> OnboardingTwoFragment()
            ON_BOARDING_THREE_PAGE -> OnboardingThreeFragment()
            else -> OnboardingOneFragment()
        }
    }

    override fun getCount(): Int {
        return MAX_PAGES
    }
}