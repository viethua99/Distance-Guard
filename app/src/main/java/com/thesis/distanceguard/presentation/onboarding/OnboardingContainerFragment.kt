package com.thesis.distanceguard.presentation.onboarding

import android.view.View
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_onboarding_container.*

class OnboardingContainerFragment : BaseFragment() {
    companion object {
        const val TAG = "OnboardingContainerFragment"
    }

    private lateinit var onboardViewPagerAdapter: OnboardViewPagerAdapter

    override fun getResLayoutId(): Int {
        return R.layout.fragment_onboarding_container
    }

    override fun onMyViewCreated(view: View) {
        setupViews()
    }

    private fun setupViews() {
        setupViewPager()
        tv_skip.setOnClickListener {
            view_pager_onboard.currentItem = 2
        }

        btn_next_step.setOnClickListener {
            if (view_pager_onboard.currentItem < 2) {
                view_pager_onboard.currentItem = view_pager_onboard.currentItem + 1
            }
        }
    }

    private fun setupViewPager() {
        onboardViewPagerAdapter =
            OnboardViewPagerAdapter(
                fragmentManager!!,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
        view_pager_onboard.adapter = onboardViewPagerAdapter
        indicator_onboard.setViewPager(view_pager_onboard)

        view_pager_onboard.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    tv_skip.visibility = View.GONE
                    btn_next_step.visibility = View.GONE
                } else {
                    tv_skip.visibility = View.VISIBLE
                    btn_next_step.visibility = View.VISIBLE
                }
            }
        })
    }
}