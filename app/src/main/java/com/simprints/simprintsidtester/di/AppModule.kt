package com.simprints.simprintsidtester.di

import com.google.gson.GsonBuilder
import com.simprints.simprintsidtester.fragments.edit.IntentEditViewModel
import com.simprints.simprintsidtester.fragments.integration.IntegrationViewModel
import com.simprints.simprintsidtester.fragments.list.IntentListViewModel
import com.simprints.simprintsidtester.fragments.result.ResultListViewModel
import com.simprints.simprintsidtester.model.BundleTypeAdapterFactory
import com.simprints.simprintsidtester.model.local.*
import com.simprints.simprintsidtester.model.store.ProjectDataCache
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        LocalSimprintsIntentDatabase.getInstance(androidContext())
    }
    single {
        GsonBuilder().setPrettyPrinting().registerTypeAdapterFactory(BundleTypeAdapterFactory())
            .create()
    }

    single { get<LocalSimprintsIntentDatabase>().localSimprintsIntentDao() }
    single { get<LocalSimprintsIntentDatabase>().localSimprintsResultDao() }
    single<LocalSimprintsIntentDataSource> { LocalSimprintsIntentDataSourceImpl(get()) }
    single<LocalSimprintsResultDataSource> { LocalSimprintsResultDataSourceImpl(get()) }

    single { ProjectDataCache(androidContext()) }

    viewModel { IntentListViewModel(get()) }
    viewModel { IntentEditViewModel(get(), get(), get()) }
    viewModel { ResultListViewModel(get()) }
    viewModel { IntegrationViewModel(get(), get(), get()) }
}
