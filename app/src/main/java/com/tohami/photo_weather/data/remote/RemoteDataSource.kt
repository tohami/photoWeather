package com.tohami.photo_weather.data.remote

import com.tohami.photo_weather.BuildConfig
import com.tohami.photo_weather.data.model.dto.APIResponse
import com.tohami.photo_weather.data.model.dto.CurrentWeather
import com.tohami.photo_weather.data.remote.retrofit.ApiInterface
import com.tohami.photo_weather.data.remote.retrofit.RetrofitConfigurations


class RemoteDataSource(private val mRetrofitInterface: ApiInterface) : RetrofitConfigurations(),
    IRemoteDataSource {

    override fun cancelRequest(tag: String) {
        cancelRetrofitRequest(tag)
    }

    override fun getCurrentWeatherData(
        latitude: Double,
        longitude: Double,
        tag: String
    ): APIResponse<CurrentWeather>? {
        return executeAPIResponseCall(
            mRetrofitInterface.getCurrentWeatherData(
                latitude,
                longitude,
                BuildConfig.API_KEY,
                "metric"
            ), tag
        )
    }
}
