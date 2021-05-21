package com.thesis.distanceguard.room.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Viet Hua on 05/21/2021.
 */

object MapTypeConverter {

    @TypeConverter
    @JvmStatic
    fun stringToMap(value: String): HashMap<String, Long> {
        return Gson().fromJson(value,  object : TypeToken<HashMap<String, Long>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun mapToString(value: HashMap<String, Long>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }
}