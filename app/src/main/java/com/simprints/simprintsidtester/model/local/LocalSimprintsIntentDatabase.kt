package com.simprints.simprintsidtester.model.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simprints.simprintsidtester.model.domain.IntentArgument
import kotlinx.coroutines.*

@Database(entities = [LocalSimprintsIntent::class, LocalSimprintsResult::class], version = 2)
@TypeConverters(GithubTypeConverters::class)
abstract class LocalSimprintsIntentDatabase : RoomDatabase() {

    abstract fun localSimprintsIntentDao(): LocalSimprintsIntentDao
    abstract fun localSimprintsResultDao(): LocalSimprintsResultDao

    companion object {

        @Volatile
        private var INSTANCE: LocalSimprintsIntentDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope): LocalSimprintsIntentDatabase =
            INSTANCE ?: synchronized(this) {
                buildDatabase(context, scope).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context, scope: CoroutineScope) =
            Room.databaseBuilder(
                context,
                LocalSimprintsIntentDatabase::class.java,
                "localDb-intents-db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        scope.launch {
                            getInstance(context, scope).localSimprintsIntentDao()
                                .saveIntentsList(defaultIntentList)
                        }
                    }
                })
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "create table `LocalSimprintsResult` (`id` text not null, " +
                    "`dateTimeSent` text not null, " +
                    "`intentSent` text not null, " +
                    "`resultReceived` text not null, " +
                    "primary key(`id`))"
        )
    }
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
