package com.thesis.distanceguard.presentation.main.fragment

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.databinding.FragmentMainBinding
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.main.activity.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainFragment : BaseFragment<FragmentMainBinding>() {
    companion object {
        const val TAG = "MainFragment"
    }
    override fun getResLayoutId(): Int {
       return R.layout.fragment_main
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setupToolbarTitle(getString(R.string.nav_item_dashboard))
        (activity as MainActivity).appBarLayout.visibility = View.VISIBLE
        setupBottomNavigationView()
        setupViewPager()

    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }


    private fun setupBottomNavigationView() {
        Timber.d("setupBottomNavigationView")
        binding.bottomNavMain.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_item_overview -> {
                    binding.mainViewPager.currentItem = MainViewPagerAdapter.OVERVIEW_PAGE
                    true

                }
                R.id.nav_item_countries -> {
                    binding.mainViewPager.currentItem = MainViewPagerAdapter.COUNTRIES_PAGE
                    true
                }
                R.id.nav_item_scanner -> {
                    binding.mainViewPager.currentItem = MainViewPagerAdapter.SCAN_PAGE
                    true
                }
                R.id.nav_item_team -> {
                    binding.mainViewPager.currentItem = MainViewPagerAdapter.TEAM_PAGE
                    true
                }
                else -> false
            }
        }
    }

    private fun setupViewPager() {
        Timber.d("setupViewPager")
        (activity as MainActivity).supportActionBar?.title = getString(R.string.nav_item_dashboard)
        val mainViewPagerAdapter = MainViewPagerAdapter(childFragmentManager)
        binding.apply {
            mainViewPager.adapter = mainViewPagerAdapter
            mainViewPager.offscreenPageLimit = 4
            mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {}
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                override fun onPageSelected(position: Int) {
                    when (position) {
                        MainViewPagerAdapter.OVERVIEW_PAGE ->{
                            bottomNavMain.menu.findItem(R.id.nav_item_overview).isChecked = true
                            setupToolbarTitle( getString(R.string.nav_item_dashboard))
                        }
                        MainViewPagerAdapter.COUNTRIES_PAGE ->  {
                            bottomNavMain.menu.findItem(R.id.nav_item_countries).isChecked = true
                            setupToolbarTitle(getString(R.string.nav_item_countries))
                        }
                        MainViewPagerAdapter.SCAN_PAGE ->  {
                            bottomNavMain.menu.findItem(R.id.nav_item_scanner).isChecked = true
                            setupToolbarTitle(getString(R.string.nav_item_scanner))
                        }
                        MainViewPagerAdapter.TEAM_PAGE ->  {
                            bottomNavMain.menu.findItem(R.id.nav_item_team).isChecked = true
                            setupToolbarTitle(getString(R.string.nav_item_team))
                        }
                    }
                }
            })
        }

    }
}