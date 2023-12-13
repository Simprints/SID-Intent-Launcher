package com.simprints.intentlauncher.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        LocalSimprintsIntent::class,
        LocalSimprintsResult::class,
        IntentCallEntity::class
    ],
    version = 4,
)
@TypeConverters(IntentArgumentTypeConverters::class)
abstract class IntentDatabase : RoomDatabase() {

    abstract fun localSimprintsIntentDao(): LocalSimprintsIntentDao
    abstract fun localSimprintsResultDao(): LocalSimprintsResultDao
    abstract fun intentCallDao(): IntentCallDao

    companion object {

        @Volatile
        private var INSTANCE: IntentDatabase? = null

        fun getInstance(context: Context): IntentDatabase =
            INSTANCE ?: synchronized(this) { buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                IntentDatabase::class.java,
                "localDb-intents-db"
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}

