package com.tohami.photo_weather.data.remote

import com.tohami.photo_weather.data.model.dto.APIResponse
import com.tohami.photo_weather.data.model.dto.CurrentWeather

interface IRemoteDataSource {

    fun cancelRequest(tag: String)

    fun getCurrentWeatherData(
        latitude: Double,
        longitude: Double,
        tag: String
    ): APIResponse<CurrentWeather>?
}