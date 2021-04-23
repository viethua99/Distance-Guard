package com.thesis.distanceguard.presentation.countries

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.presentation.detail.DetailFragment
import com.thesis.distanceguard.presentation.main.MainActivity
import kotlinx.android.synthetic.main.fragment_countries.*
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class CountriesFragment : BaseFragment() {

    private lateinit var countriesRecyclerViewAdapter: CountriesRecyclerViewAdapter

    override fun getResLayoutId(): Int {
        return R.layout.fragment_countries
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(view!!.context)
        countriesRecyclerViewAdapter = CountriesRecyclerViewAdapter(view!!.context)
        countriesRecyclerViewAdapter.setDataList(
            listOf("France", "France", "France", "France", "France", "France", "France", "France", "France", "France", "France", "France",
                "France", "France", "France", "France", "France", "France", "France", "France"
            )
        )
        countriesRecyclerViewAdapter.itemClickListener = object :
            BaseRecyclerViewAdapter.ItemClickListener<String> {
            override fun onClick(position: Int, item: String) {
                Timber.d("onClick: $item")
                ViewCompat.postOnAnimationDelayed(view!!, // Delay to show ripple effect
                    Runnable {
                        val mainActivity = activity as MainActivity
                        mainActivity.addFragment(DetailFragment(), DetailFragment.TAG,R.id.container_main)
                    }
                    , 50)

            }

            override fun onLongClick(position: Int, item: String) {}
        }

        rv_countries.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = countriesRecyclerViewAdapter
        }
    }
}