package com.simprints.intentlauncher.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        IntentCallEntity::class
    ],
    version = 5,
)
abstract class IntentDatabase : RoomDatabase() {

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
