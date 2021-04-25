package com.thesis.distanceguard.presentation.countries

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter

/**
 * Created by Viet Hua on 04/10/2021.
 */

class CountriesRecyclerViewAdapter(context: Context) :
    BaseRecyclerViewAdapter<CountryResponse, CountriesRecyclerViewAdapter.ViewHolder>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.renderUI(data)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var imgCountryFlag: ImageView = view.findViewById(R.id.img_country_flag)
        var tvCountryName: TextView = view.findViewById(R.id.tv_country_name)
        var tvConfirmCount: TextView = view.findViewById(R.id.tv_country_confirm_count)
        var tvRecoveredCount: TextView = view.findViewById(R.id.tv_country_recovered_count)
        var tvDeathsCount: TextView = view.findViewById(R.id.tv_country_death_count)


        init {
            view.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            itemClickListener.onClick(adapterPosition, dataList[adapterPosition])
        }


        fun renderUI(data: CountryResponse) {
            tvCountryName.text = data.country
            tvConfirmCount.text = data.cases.toString()
            tvRecoveredCount.text =data.recovered.toString()
            tvDeathsCount.text = data.deaths.toString()
            Glide
                .with(itemView.context)
                .load(data.countryInfo.flag)
                .centerCrop()
                .into(imgCountryFlag)

        }
    }
}