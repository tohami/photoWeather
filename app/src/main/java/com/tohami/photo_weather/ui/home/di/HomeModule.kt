package com.tohami.photo_weather.ui.home.di

import com.tohami.photo_weather.ui.home.repository.HomeRepository
import com.tohami.photo_weather.ui.home.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val homeModule = module {

    single {
        HomeRepository(get(), get(), get())
    }

    viewModel {
        HomeViewModel(get())
    }

}