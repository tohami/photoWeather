package com.tohami.photo_weather.data.local

import com.tohami.photo_weather.data.local.room.WeatherPhotoDB
import com.tohami.photo_weather.data.local.room.WeatherPhotoDao
import com.tohami.photo_weather.data.model.WeatherPhoto
import io.reactivex.Completable
import io.reactivex.Observable

class LocalDataSource(private val weatherPhotoDB: WeatherPhotoDB) : ILocalDataSource {


    private val weatherDataDao: WeatherPhotoDao = weatherPhotoDB.weatherPhotoDao

    override fun getWeatherPhotoList(): Observable<List<WeatherPhoto>?> {
        return weatherDataDao.weatherPhotoList()
    }

    override fun saveWeatherPhoto(weatherPhoto: WeatherPhoto): Completable {
        return weatherDataDao.insertWeatherPhoto(weatherPhoto)
    }
}