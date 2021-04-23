package com.thesis.distanceguard.presentation.scanner

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseFragment
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.presentation.countries.CountriesRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_countries.*
import kotlinx.android.synthetic.main.fragment_scanner.*
import timber.log.Timber

/**
 * Created by Viet Hua on 04/10/2021.
 */

class ScannerFragment : BaseFragment() {

    private lateinit var scannerRecyclerViewAdapter: ScannerRecyclerViewAdapter

    override fun getResLayoutId(): Int {
        return R.layout.fragment_scanner
    }

    override fun onMyViewCreated(view: View) {
        Timber.d("onMyViewCreated")
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        Timber.d("setupRecyclerView")
        val linearLayoutManager = LinearLayoutManager(view!!.context)
        scannerRecyclerViewAdapter = ScannerRecyclerViewAdapter(view!!.context)
        scannerRecyclerViewAdapter.setDataList(
            listOf("53", "53", "53", "53", "53", "53", "53", "53", "53", "53", "53", "53",
                "53", "53", "53", "53", "53", "53", "53", "53"
            )
        )

        rv_scanner.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = scannerRecyclerViewAdapter
        }
    }
}