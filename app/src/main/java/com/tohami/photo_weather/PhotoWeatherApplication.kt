package com.tohami.photo_weather

import android.app.Application
import com.tohami.photo_weather.di.appModule
import com.tohami.photo_weather.ui.home.di.homeModule
import com.tohami.photo_weather.ui.preview.di.previewModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class PhotoWeatherApplication : Application() {

    companion object {
        lateinit var instance: PhotoWeatherApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        startKoin {
            androidLogger()
            androidContext(this@PhotoWeatherApplication)
            modules(appModule, previewModule, homeModule)
        }
    }

}