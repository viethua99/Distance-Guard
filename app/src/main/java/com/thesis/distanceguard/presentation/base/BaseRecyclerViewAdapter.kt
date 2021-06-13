package com.thesis.distanceguard.presentation.base

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T, VH : RecyclerView.ViewHolder>(val context: Context) :
    RecyclerView.Adapter<VH>() {
    val dataList = mutableListOf<T>()
    protected val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    lateinit var itemClickListener: ItemClickListener<T>

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(list: List<T>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }


    fun clear() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun removeByPosition(position: Int) {
        if (position < 0) return
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position, dataList.size)
    }

    interface ItemClickListener<T> {
        fun onClick(position: Int, item: T)
        fun onLongClick(position: Int,item: T)
    }
}