package com.simprints.simprintsidtester.di

import androidx.room.Room
import com.simprints.simprintsidtester.fragments.edit.IntentEditViewModel
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel
import com.simprints.simprintsidtester.model.local.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class KoinCoreModule {
    val appModule = module {
        single {
            Room
                .databaseBuilder(
                    androidApplication(),
                    LocalSimprintsIntentDatabase::class.java,
                    "localDb-db"
                )
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .build()
        }

        single { get<LocalSimprintsIntentDatabase>().localSimprintsIntentDao() }
        single { get<LocalSimprintsIntentDatabase>().localSimprintsResultDao() }
        single<LocalSimprintsIntentDataSource> { LocalSimprintsIntentDataSourceImpl() }
        single<LocalSimprintsresultDataSource> { LocalSimprintsresultDataSourceImpl() }
        viewModel { IntentListViewModel() }
        viewModel { IntentEditViewModel() }
    }
}