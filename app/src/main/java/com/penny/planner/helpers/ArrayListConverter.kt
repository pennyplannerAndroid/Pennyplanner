package com.penny.planner.helpers

import androidx.room.TypeConverter
import com.google.gson.Gson

class ArrayListConverter {

    @TypeConverter
    fun fromStringArrayList(value: List<String>): String {

        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringArrayList(value: String): List<String> {
        return try {
            Gson().fromJson<List<String>>(value) //using extension function
        } catch (e: Exception) {
            arrayListOf()
        }
    }
}