package com.simprints.intentlauncher.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.simprints.intentlauncher.data.db.IntentDatabase
import com.simprints.intentlauncher.tools.BundleTypeAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext context: Context): IntentDatabase =
        IntentDatabase.getInstance(context)

    @Provides
    fun provideIntentCallEntityDao(db: IntentDatabase) = db.intentCallDao()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapterFactory(
            BundleTypeAdapterFactory()
        )
        .create()
}
