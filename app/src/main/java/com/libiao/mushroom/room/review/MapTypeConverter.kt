package com.libiao.mushroom.room.review

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MapTypeConverter {
    @TypeConverter
    fun stringToMap(value: String): Map<Double, Int> {
        return Gson().fromJson(value, object : TypeToken<Map<Double, Int>>() {}.type)
    }
    @TypeConverter
    fun mapToString(value: Map<Double, Int>?): String {
        return if (value == null) "" else Gson().toJson(value)
    }
}