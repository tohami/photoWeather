package com.tohami.photo_weather.data.local

import com.tohami.photo_weather.data.model.WeatherPhoto
import io.reactivex.Completable
import io.reactivex.Observable

interface ILocalDataSource {

    fun getWeatherPhotoList(): Observable<List<WeatherPhoto>?>

    fun saveWeatherPhoto(weatherPhoto: WeatherPhoto): Completable

}
