package com.simprints.simprintsidtester.di

import androidx.room.Room
import com.simprints.simprintsidtester.fragments.edit.IntentEditViewModel
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel
import com.simprints.simprintsidtester.model.local.LocalSimprintsIntentDataSource
import com.simprints.simprintsidtester.model.local.LocalSimprintsIntentDataSourceImpl
import com.simprints.simprintsidtester.model.local.LocalSimprintsIntentDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

class KoinCoreModule {
    val appModule = module {
        single {
            Room.databaseBuilder(androidApplication(), LocalSimprintsIntentDatabase::class.java, "localDb-db")
                .allowMainThreadQueries()
                .build()
        }

        single { get<LocalSimprintsIntentDatabase>().localSimprintsIntentDao() }
        single<LocalSimprintsIntentDataSource> { LocalSimprintsIntentDataSourceImpl() }
        viewModel { IntentListViewModel() }
        viewModel { IntentEditViewModel() }
    }
}