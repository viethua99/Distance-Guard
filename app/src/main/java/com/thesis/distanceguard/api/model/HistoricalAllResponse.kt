package com.thesis.distanceguard.api.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HistoricalAllResponse(
    @SerializedName("cases")
    val cases: HashMap<String, Float>?,
    @SerializedName("deaths")
    val deaths: HashMap<String, Float>?,
    @SerializedName("recovered")
    val recovered: HashMap<String, Float>?
) : Parcelable
