package com.simprints.intentlauncher.model.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.intentlauncher.model.domain.IntentArgument

class IntentArgumentTypeConverters {

    private var gson = Gson()

    @TypeConverter
    fun stringToSomeObjectList(data: String): List<IntentArgument> {
        val listType = object : TypeToken<List<IntentArgument>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun someObjectListToString(someObjects: List<IntentArgument>): String {
        return gson.toJson(someObjects)
    }
}