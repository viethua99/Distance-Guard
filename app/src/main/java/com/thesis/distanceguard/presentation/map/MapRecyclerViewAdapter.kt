package com.thesis.distanceguard.presentation.map

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.util.AppUtil

class MapRecyclerViewAdapter(context: Context) :
    BaseRecyclerViewAdapter<CountryResponse, MapRecyclerViewAdapter.ViewHolder>(context) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MapRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (dataList.isEmpty()) {
            0
        } else {
            dataList.size
        }
    }

    override fun onBindViewHolder(holder: MapRecyclerViewAdapter.ViewHolder, position: Int) {
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

        override fun onClick(v: View?) {

        }

        fun renderUI(data: CountryResponse) {
            tvCountryName.text = data.country
            tvConfirmCount.text = AppUtil.toNumberWithCommas(data.cases.toLong())
            tvRecoveredCount.text = AppUtil.toNumberWithCommas(data.recovered.toLong())
            tvDeathsCount.text = AppUtil.toNumberWithCommas(data.deaths.toLong())
            tvUpdateTime.text =
                "Last update ${AppUtil.convertMillisecondsToDateFormat(data.updated)}"
            Glide
                .with(itemView.context)
                .load(data.countryInfo.flag)
                .centerCrop()
                .into(imgCountryFlag)

        }

    }

    interface ItemClickListener<T> {
        fun onClick(position: Int, item: T)
        fun onLongClick(position: Int, item: T)
    }

}