package com.thesis.distanceguard.presentation.main.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.thesis.distanceguard.presentation.map.MapFragment
import com.thesis.distanceguard.presentation.overview.OverviewFragment

class MainViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        const val OVERVIEW_PAGE = 0
        const val MAP_PAGE = 1
        const val MAX_PAGES = 2
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> OverviewFragment()
            else -> MapFragment()
        }
    }

    override fun getCount(): Int {
        return MAX_PAGES
    }
}