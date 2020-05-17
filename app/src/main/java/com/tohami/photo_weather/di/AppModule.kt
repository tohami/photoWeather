package com.tohami.photo_weather.di

import com.tohami.photo_weather.data.local.LocalDataSource
import com.tohami.photo_weather.BuildConfig
import com.tohami.photo_weather.data.local.ILocalDataSource
import com.tohami.photo_weather.data.local.room.WeatherPhotoDB.Companion.getDatabaseInstance
import com.tohami.photo_weather.data.remote.IRemoteDataSource
import com.tohami.photo_weather.data.remote.RemoteDataSource
import com.tohami.photo_weather.data.remote.retrofit.ApiInterface
import com.tohami.photo_weather.utils.ConnectionUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    single {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }


        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .retryOnConnectionFailure(true)
            .build()
    }

    single {
        Retrofit.Builder().baseUrl(BuildConfig.API_URL)
            .client(get<OkHttpClient>())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(ApiInterface::class.java)
    }

    single {
        ConnectionUtils(get())
    }
    single {
        getDatabaseInstance(get())
    }

    single<IRemoteDataSource> {
        RemoteDataSource(get())
    }

    single<ILocalDataSource> {
        LocalDataSource(get())
    }
}
