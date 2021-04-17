package com.thesis.distanceguard.presentation.main.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.thesis.distanceguard.presentation.countries.CountriesFragment
import com.thesis.distanceguard.presentation.dashboard.DashboardFragment
import com.thesis.distanceguard.presentation.scan.ScanFragment
import com.thesis.distanceguard.presentation.team.TeamFragment

class MainViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        const val OVERVIEW_PAGE = 0
        const val COUNTRIES_PAGE = 1
        const val SCAN_PAGE = 2
        const val TEAM_PAGE = 3

        const val MAX_PAGES = 4
    }

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> DashboardFragment()
            1 -> CountriesFragment()
            2 -> ScanFragment()
            else -> TeamFragment()
        }
    }

    override fun getCount(): Int {
        return MAX_PAGES
    }
}