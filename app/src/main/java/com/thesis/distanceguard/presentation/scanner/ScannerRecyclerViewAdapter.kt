package com.thesis.distanceguard.presentation.scanner

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thesis.distanceguard.R
import com.thesis.distanceguard.ble_module.dao.Device
import com.thesis.distanceguard.ble_module.util.BluetoothUtils
import com.thesis.distanceguard.ble_module.util.Constants
import com.thesis.distanceguard.presentation.base.BaseRecyclerViewAdapter

class ScannerRecyclerViewAdapter(context: Context) :
    BaseRecyclerViewAdapter<Device, ScannerRecyclerViewAdapter.ViewHolder>(context) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = layoutInflater.inflate(R.layout.item_scanner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.renderUI(data)
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvStrength: TextView = view.findViewById(R.id.tv_device_strength)
        var tvWarnMessage: TextView = view.findViewById(R.id.tv_warn_message)


        fun renderUI(data: Device) {
            // Notify the user when we are adding a device that's too close
            val signal = BluetoothUtils.calculateSignal(data.rssi, data.txPower, data.isAndroid)
            tvStrength.text = signal.toString()
            when {
                signal <= Constants.SIGNAL_DISTANCE_OK -> {
                    tvWarnMessage.setTextColor(ContextCompat.getColor(context, R.color.primary_green))
                    tvWarnMessage.text = context.resources.getString(R.string.item_scanner_safer)
                }
                signal <= Constants.SIGNAL_DISTANCE_LIGHT_WARN -> {
                    tvWarnMessage.setTextColor(ContextCompat.getColor(context, R.color.primary_orange))
                    tvWarnMessage.text = context.resources.getString(R.string.item_scanner_warning)
                }
                signal <= Constants.SIGNAL_DISTANCE_STRONG_WARN -> {
                    tvWarnMessage.setTextColor(ContextCompat.getColor(context, R.color.primary_orange))
                    tvWarnMessage.text = context.resources.getString(R.string.item_scanner_strong_warning)
                }
                else -> {
                    tvWarnMessage.setTextColor(ContextCompat.getColor(context, R.color.primary_red))
                    tvWarnMessage.text = context.resources.getString(R.string.item_scanner_too_close)
                }
            }
            if (data.isTeamMember) {
                tvWarnMessage.setTextColor(ContextCompat.getColor(context, R.color.primary_green))
                tvWarnMessage.text = context.resources.getString(R.string.item_scanner_safer)

            }
        }
    }
}