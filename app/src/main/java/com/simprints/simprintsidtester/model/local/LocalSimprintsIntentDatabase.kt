package com.simprints.simprintsidtester.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.simprintsidtester.model.domain.IntentArgument


@Database(entities = [LocalSimprintsIntent::class], version = 1)
@TypeConverters(GithubTypeConverters::class)
abstract class LocalSimprintsIntentDatabase : RoomDatabase() {

    abstract fun localSimprintsIntentDao(): LocalSimprintsIntentIntentDao
}

class GithubTypeConverters {

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