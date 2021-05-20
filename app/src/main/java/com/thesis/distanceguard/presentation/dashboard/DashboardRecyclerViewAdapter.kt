package com.thesis.distanceguard.presentation.dashboard

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.thesis.distanceguard.R
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.util.AppUtil

class DashboardRecyclerViewAdapter(context: Context) :
    BaseRecyclerViewAdapter<CountryResponse, DashboardRecyclerViewAdapter.ViewHolder>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_top_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.renderUI(data,position)
    }

    override fun getItemCount(): Int {
        return if(dataList.size > 5){
            5
        } else {
            dataList.size
        }
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) , View.OnClickListener{
        var imgCountryFlag: ImageView = view.findViewById(R.id.img_country_flag)
        var tvCountryName: TextView = view.findViewById(R.id.tv_country_name)
        var tvTodayCaseCount: TextView = view.findViewById(R.id.tv_today_cases_count)
        var tvTodayRecoveredCount: TextView = view.findViewById(R.id.tv_today_recovered_count)
        var tvTodayDeathsCount: TextView = view.findViewById(R.id.tv_today_deaths_count)
        var clTopCountryBackground: ConstraintLayout = view.findViewById(R.id.cl_top_background)

        init {
            view.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            itemClickListener.onClick(adapterPosition, dataList[adapterPosition])

        }
        fun renderUI(data: CountryResponse,position: Int) {
            when(position){
                0 -> {
                    clTopCountryBackground.background = ContextCompat.getDrawable(context,R.drawable.background_gradient_top_one)
                }
                1 -> {
                    clTopCountryBackground.background = ContextCompat.getDrawable(context,R.drawable.background_gradient_top_two)
                }
                2 -> {
                    clTopCountryBackground.background = ContextCompat.getDrawable(context,R.drawable.background_gradient_top_three)
                }
                3 -> {
                    clTopCountryBackground.background = ContextCompat.getDrawable(context,R.drawable.background_gradient_top_four)
                }
                4 -> {
                    clTopCountryBackground.background = ContextCompat.getDrawable(context,R.drawable.background_gradient_top_five)
                }
            }
            tvCountryName.text = data.country
            tvTodayCaseCount.text = "+" + AppUtil.toNumberWithCommas(data.todayCases.toLong())
            tvTodayRecoveredCount.text = "+" + AppUtil.toNumberWithCommas(data.todayRecovered.toLong())
            tvTodayDeathsCount.text = "+" + AppUtil.toNumberWithCommas(data.todayDeaths.toLong())
            Glide
                .with(itemView.context)
                .load(data.countryInfo.flag)
                .centerCrop()
                .into(imgCountryFlag)

        }

    }
}