package com.tohami.photo_weather.ui.home.view

import androidx.navigation.fragment.FragmentNavigator
import com.tohami.photo_weather.data.model.WeatherPhoto

interface OnWeatherPhotoClickListener {
    fun onWeatherPhotoClick(
        weatherPhoto: WeatherPhoto,
        extras: FragmentNavigator.Extras
    )
}