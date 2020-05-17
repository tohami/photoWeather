package com.tohami.photo_weather.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.tohami.photo_weather.data.local.room.CurrentWeatherConverter
import com.tohami.photo_weather.data.model.dto.CurrentWeather
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "weatherPhoto")
data class WeatherPhoto(@field:TypeConverters(CurrentWeatherConverter::class) val currentWeather: CurrentWeather, @PrimaryKey val photoPath: String) :
    Parcelable