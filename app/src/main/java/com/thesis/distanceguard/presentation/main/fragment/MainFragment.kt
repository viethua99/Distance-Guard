package com.thesis.distanceguard.presentation.main.fragment

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.MainActivity
import kotlinx.android.synthetic.main.fragment_main.*
import timber.log.Timber

class MainFragment : BaseFragment() {
    companion object {
        const val TAG = "MainFragment"
    }
    override fun getResLayoutId(): Int {
       return R.layout.fragment_main
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")

        setupBottomNavigationView()
        setupViewPager()

    }

    private fun setupBottomNavigationView() {
        Timber.d("setupBottomNavigationView")
        bottom_nav_main.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_item_overview -> {
                    main_view_pager.currentItem = MainViewPagerAdapter.OVERVIEW_PAGE
                    true

                }
                R.id.nav_item_map -> {
                    main_view_pager.currentItem = MainViewPagerAdapter.MAP_PAGE
                    true
                }
                else -> false
            }
        }
    }

    private fun setupViewPager() {
        Timber.d("setupViewPager")
        (activity as MainActivity).supportActionBar?.title = getString(R.string.nav_item_overview)
        val mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager)
        main_view_pager.adapter = mainViewPagerAdapter
        main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                when (position) {
                    MainViewPagerAdapter.OVERVIEW_PAGE ->{
                        bottom_nav_main.menu.findItem(R.id.nav_item_overview).isChecked = true
                        (activity as MainActivity).supportActionBar?.title = getString(R.string.nav_item_overview)
                    }
                    MainViewPagerAdapter.MAP_PAGE ->  {
                        bottom_nav_main.menu.findItem(R.id.nav_item_map).isChecked = true
                        (activity as MainActivity).supportActionBar?.title = getString(R.string.nav_item_map)
                    }
                }
            }
        })
    }
}