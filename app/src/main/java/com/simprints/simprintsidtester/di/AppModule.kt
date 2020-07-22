package com.simprints.simprintsidtester.di

import androidx.room.Room
import com.simprints.simprintsidtester.fragments.edit.IntentEditViewModel
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel
import com.simprints.simprintsidtester.fragments.result.ResultListViewModel
import com.simprints.simprintsidtester.model.local.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room
            .databaseBuilder(
                androidContext(),
                LocalSimprintsIntentDatabase::class.java,
                "localDb-db"
            )
            .addMigrations(MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()
    }

    single { get<LocalSimprintsIntentDatabase>().localSimprintsIntentDao() }
    single { get<LocalSimprintsIntentDatabase>().localSimprintsResultDao() }
    single<LocalSimprintsIntentDataSource> { LocalSimprintsIntentDataSourceImpl(get()) }
    single<LocalSimprintsResultDataSource> { LocalSimprintsResultDataSourceImpl(get()) }

    viewModel { IntentListViewModel(get()) }
    viewModel { IntentEditViewModel(get(), get()) }
    viewModel { ResultListViewModel(get()) }
}