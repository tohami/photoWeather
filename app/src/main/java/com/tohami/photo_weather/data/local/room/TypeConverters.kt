package com.tohami.photo_weather.data.local.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.tohami.photo_weather.data.model.dto.CurrentWeather


class CurrentWeatherConverter {
    @TypeConverter
    fun getCurrentWeather(value: String): CurrentWeather {
        return Gson().fromJson(value, CurrentWeather::class.java)
    }

    @TypeConverter
    fun languagesToStoredString(value: CurrentWeather): String {
        return Gson().toJson(value)
    }
}