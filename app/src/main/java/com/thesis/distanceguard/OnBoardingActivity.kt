package com.thesis.distanceguard

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import me.relex.circleindicator.CircleIndicator

class OnBoardingActivity: AppCompatActivity() {

    private lateinit var tvSkip:TextView
    private lateinit var nextBtn:Button
    private lateinit var viewPager: ViewPager
    private lateinit var circleIndicator: CircleIndicator
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        initUI()

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.setAdapter(viewPagerAdapter)
        circleIndicator.setViewPager(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
            override fun onPageSelected(position: Int) {
                if(position == 2){
                    tvSkip.visibility = View.GONE
                    nextBtn.visibility = View.GONE
                } else {
                    tvSkip.visibility = View.VISIBLE
                    nextBtn.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun initUI() {
        tvSkip = findViewById(R.id.skip_tv)
        nextBtn = findViewById(R.id.next_btn)
        viewPager = findViewById(R.id.view_pager)
        circleIndicator = findViewById(R.id.circle_indicator)
        relativeLayout = findViewById(R.id.next_button_layout)

        tvSkip.setOnClickListener{
            viewPager.setCurrentItem(2)
        }
        nextBtn.setOnClickListener{
            if(viewPager.getCurrentItem() < 2){
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1)
            }
        }
    }


}
