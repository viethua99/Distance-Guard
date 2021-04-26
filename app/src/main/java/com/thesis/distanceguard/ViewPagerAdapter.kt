package com.thesis.distanceguard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm:FragmentManager, behavior: Int):
    FragmentStatePagerAdapter(fm, behavior) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0-> return WashYourHandsFragment()
            1-> return WearMaskFragment()
            2-> return UseRoseRagFragment()
            else-> return WashYourHandsFragment()
        }
    }

    override fun getCount(): Int {
        return 3;
    }
}