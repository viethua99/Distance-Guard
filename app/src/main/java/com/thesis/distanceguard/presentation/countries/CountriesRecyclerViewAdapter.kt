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
import com.thesis.distanceguard.util.AppUtil
import kotlinx.android.synthetic.main.fragment_dashboard.*

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
        var tvConfirmCount: TextView = view.findViewById(R.id.tv_total_cases_count)
        var tvRecoveredCount: TextView = view.findViewById(R.id.tv_total_recovered_count)
        var tvDeathsCount: TextView = view.findViewById(R.id.tv_total_death_count)
        var tvUpdateTime: TextView = view.findViewById(R.id.tv_update_time)


        init {
            view.setOnClickListener(this)

        }

        override fun onClick(p0: View?) {
            itemClickListener.onClick(adapterPosition, dataList[adapterPosition])
        }


        fun renderUI(data: CountryResponse) {
            tvCountryName.text = data.country
            tvConfirmCount.text = AppUtil.toNumberWithCommas(data.cases.toLong())
            tvRecoveredCount.text = AppUtil.toNumberWithCommas(data.recovered.toLong())
            tvDeathsCount.text =  AppUtil.toNumberWithCommas(data.deaths.toLong())
            tvUpdateTime.text = "Last update ${AppUtil.convertMillisecondsToDateFormat(data.updated)}"
            Glide
                .with(itemView.context)
                .load(data.countryInfo.flag)
                .centerCrop()
                .into(imgCountryFlag)

        }
    }
}