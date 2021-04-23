package com.thesis.distanceguard.presentation.scanner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter

class ScannerRecyclerViewAdapter(context: Context) :
    BaseRecyclerViewAdapter<String, ScannerRecyclerViewAdapter.ViewHolder>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_scanner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.renderUI(data)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvRssi: TextView = view.findViewById(R.id.tv_device_rssi)


        fun renderUI(data: String) {
            tvRssi.text = data
        }
    }
}