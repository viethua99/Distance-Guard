package com.thesis.distanceguard.presentation.countries

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.thesis.distanceguard.R
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter

/**
 * Created by Viet Hua on 04/10/2021.
 */

class CountriesRecyclerViewAdapter(context: Context) :
    BaseRecyclerViewAdapter<String, CountriesRecyclerViewAdapter.ViewHolder>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.renderUI(data)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var tvCountryName: TextView = view.findViewById(R.id.tv_country_name)


        init {
            view.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            itemClickListener.onClick(adapterPosition, dataList[adapterPosition])
        }


        fun renderUI(data: String) {
            tvCountryName.text = data


        }
    }
}