package com.thesis.distanceguard.presentation.map

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter

class MapRecyclerViewAdapter(context:Context) : BaseRecyclerViewAdapter<CountryResponse,MapRecyclerViewAdapter.ViewHolder>(context) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MapRecyclerViewAdapter.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MapRecyclerViewAdapter.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

}