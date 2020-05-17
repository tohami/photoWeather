package com.tohami.photo_weather.ui.preview.di

import com.tohami.photo_weather.ui.preview.repository.PreviewRepository
import com.tohami.photo_weather.ui.preview.viewmodel.PreviewViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val previewModule = module {

    single {
        PreviewRepository(get(), get(), get())
    }

    viewModel { (latitude: Double, longitude: Double, photoPath: String) ->
        PreviewViewModel(get(), latitude, longitude, photoPath)
    }

}