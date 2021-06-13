package com.thesis.distanceguard.presentation.onboarding

import android.view.View
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.databinding.FragmentOnboardingContainerBinding
import com.thesis.distanceguard.presentation.base.BaseFragment

class OnboardingContainerFragment : BaseFragment<FragmentOnboardingContainerBinding>() {
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
        binding.tvSkip.setOnClickListener {
            binding.viewPagerOnboard.currentItem = 2
        }

        binding.btnNextStep.setOnClickListener {
            if (binding.viewPagerOnboard.currentItem < 2) {
                binding.viewPagerOnboard.currentItem = binding.viewPagerOnboard.currentItem + 1
            }
        }
    }

    private fun setupViewPager() {
        onboardViewPagerAdapter =
            OnboardViewPagerAdapter(
                fragmentManager!!,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
        binding.viewPagerOnboard.adapter = onboardViewPagerAdapter
        binding.indicatorOnboard.setViewPager(binding.viewPagerOnboard)

        binding.viewPagerOnboard.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
                    binding.tvSkip.visibility = View.GONE
                    binding.btnNextStep.visibility = View.GONE
                } else {
                    binding.tvSkip.visibility = View.VISIBLE
                    binding.btnNextStep.visibility = View.VISIBLE
                }
            }
        })
    }
}