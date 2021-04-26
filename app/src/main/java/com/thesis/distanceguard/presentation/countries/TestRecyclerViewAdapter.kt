package com.thesis.distanceguard.presentation.countries

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.bumptech.glide.Glide
import com.thesis.distanceguard.R
import com.thesis.distanceguard.api.model.CountryResponse
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter
import com.thesis.distanceguard.util.AppUtil
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class TestRecyclerViewAdapter(private val context:Context): RecyclerView.Adapter<TestRecyclerViewAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener<CountryResponse>

    companion object {
         fun filter(
            models: List<CountryResponse>,
            query: String
        ): List<CountryResponse>? {
            val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
            val filteredModelList: MutableList<CountryResponse> = ArrayList()
            for (model in models) {
                val text: String = model.country.toLowerCase(Locale.ROOT)
                if (text.contains(lowerCaseQuery)) {
                    filteredModelList.add(model)
                }
            }
            return filteredModelList
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_country,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return sortedList.size()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = sortedList[position]
        holder.renderUI(data)
    }

    fun add(models: List<CountryResponse>) {
        sortedList.addAll(models)
    }

    fun replaceAll(models: List<CountryResponse>) {
        sortedList.beginBatchedUpdates()
        for (i in sortedList.size() - 1 downTo 0) {
            val model: CountryResponse = sortedList.get(i)
            if (!models.contains(model)) {
                sortedList.remove(model)
            }
        }
        sortedList.addAll(models)
        sortedList.endBatchedUpdates()
    }


    private val callback : SortedList.Callback<CountryResponse> = object : SortedList.Callback<CountryResponse>(){
        override fun areItemsTheSame(item1: CountryResponse, item2: CountryResponse): Boolean {
            return item1.country == item2.country

        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)

        }

        override fun onChanged(position: Int, count: Int) {
            notifyItemRangeChanged(position, count)

        }

        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position,count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)

        }

        override fun compare(o1: CountryResponse?, o2: CountryResponse?): Int {
                val comparator = Comparator<CountryResponse>{ a,b ->
                    a.country.compareTo(b.country)
                }
           return comparator.compare(o1,o2)
        }

        override fun areContentsTheSame(
            oldItem: CountryResponse,
            newItem: CountryResponse?
        ): Boolean {
            return oldItem == newItem
        }
    }

     val sortedList = SortedList<CountryResponse>(CountryResponse::class.java,callback)

    inner class ViewHolder(view:View): RecyclerView.ViewHolder(view),View.OnClickListener {
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
            itemClickListener.onClick(adapterPosition, sortedList[adapterPosition])

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

    interface ItemClickListener<T> {
        fun onClick(position: Int, item: T)
        fun onLongClick(position: Int,item: T)
    }
}