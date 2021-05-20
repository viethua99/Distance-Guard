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
import com.thesis.distanceguard.retrofit.response.CountryResponse
import com.thesis.distanceguard.room.entities.CountryEntity
import com.thesis.distanceguard.util.AppUtil
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MapAdapter(private val context:Context): RecyclerView.Adapter<MapAdapter.ViewHolder>() {
    lateinit var itemClickListener: ItemClickListener<CountryEntity>

    companion object {
        fun filter(
            models: List<CountryEntity>,
            query: String
        ): List<CountryEntity>? {
            val lowerCaseQuery = query.toLowerCase(Locale.ROOT)
            val filteredModelList: MutableList<CountryEntity> = ArrayList()
            for (model in models) {
                val text: String = model.country!!.toLowerCase(Locale.ROOT)
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

    fun add(models: List<CountryEntity>) {
        sortedList.addAll(models)
    }

    fun replaceAll(models: List<CountryEntity>) {
        sortedList.beginBatchedUpdates()
        for (i in sortedList.size() - 1 downTo 0) {
            val model: CountryEntity = sortedList.get(i)
            if (!models.contains(model)) {
                sortedList.remove(model)
            }
        }
        sortedList.addAll(models)
        sortedList.endBatchedUpdates()
    }


    private val callback : SortedList.Callback<CountryEntity> = object : SortedList.Callback<CountryEntity>(){
        override fun areItemsTheSame(item1: CountryEntity, item2: CountryEntity): Boolean {
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

        override fun compare(o1: CountryEntity?, o2: CountryEntity?): Int {
            val comparator = Comparator<CountryEntity>{ a,b ->
                a.country!!.compareTo(b.country!!)
            }
            return comparator.compare(o1,o2)
        }

        override fun areContentsTheSame(
            oldItem: CountryEntity,
            newItem: CountryEntity?
        ): Boolean {
            return oldItem == newItem
        }
    }

    val sortedList = SortedList<CountryEntity>(CountryEntity::class.java,callback)

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


        fun renderUI(data: CountryEntity) {
            tvCountryName.text = data.country
            tvConfirmCount.text = AppUtil.toNumberWithCommas(data.cases!!.toLong())
            tvRecoveredCount.text = AppUtil.toNumberWithCommas(data.recovered!!.toLong())
            tvDeathsCount.text =  AppUtil.toNumberWithCommas(data.deaths!!.toLong())
            tvUpdateTime.text = "Last update ${AppUtil.convertMillisecondsToDateFormat(data.updated!!)}"
            Glide
                .with(itemView.context)
                .load(data.countryInfoEntity!!.flag)
                .centerCrop()
                .into(imgCountryFlag)

        }
    }

    interface ItemClickListener<T> {
        fun onClick(position: Int, item: T)
        fun onLongClick(position: Int,item: T)
    }
}