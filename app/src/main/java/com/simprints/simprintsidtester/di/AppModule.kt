package com.simprints.simprintsidtester.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.simprints.simprintsidtester.model.BundleTypeAdapterFactory
import com.simprints.simprintsidtester.model.local.LocalSimprintsIntentDatabase
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
    fun provideDb(@ApplicationContext context: Context): LocalSimprintsIntentDatabase =
        LocalSimprintsIntentDatabase.getInstance(context)

    @Provides
    fun provideIntentsDao(db: LocalSimprintsIntentDatabase) = db.localSimprintsIntentDao()

    @Provides
    fun provideResultsDao(db: LocalSimprintsIntentDatabase) = db.localSimprintsResultDao()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapterFactory(
            BundleTypeAdapterFactory()
        )
        .create()
}
