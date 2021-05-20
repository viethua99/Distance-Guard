package com.thesis.distanceguard.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistoricalWorldwideResponse(
    @SerializedName("cases")
    val cases: HashMap<String, Long>,
    @SerializedName("deaths")
    val deaths: HashMap<String, Long>,
    @SerializedName("recovered")
    val recovered: HashMap<String, Long>
) : Parcelable
