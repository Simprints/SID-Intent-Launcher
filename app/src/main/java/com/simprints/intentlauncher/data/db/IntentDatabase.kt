package com.simprints.intentlauncher.data.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        IntentCallEntity::class,
        IntentPresetEntity::class,
    ],
    autoMigrations = [
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
    ],
    exportSchema = true,
    version = 7,
)
abstract class IntentDatabase : RoomDatabase() {

    abstract fun intentCallDao(): IntentCallDao

    abstract fun intentPresetDao(): IntentPresetDao

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
