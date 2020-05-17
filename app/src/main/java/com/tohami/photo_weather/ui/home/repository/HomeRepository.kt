package com.tohami.photo_weather.ui.home.repository

import com.tohami.photo_weather.ui.base.BaseRepository
import com.tohami.photo_weather.data.local.ILocalDataSource
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.data.remote.IRemoteDataSource
import com.tohami.photo_weather.utils.ConnectionUtils
import io.reactivex.Observable

class HomeRepository(
    mIRemoteDataSource: IRemoteDataSource,
    mILocalDataSource: ILocalDataSource,
    connectionUtils: ConnectionUtils
) : BaseRepository(mIRemoteDataSource, mILocalDataSource, connectionUtils) {


    fun weatherPhotoList(): Observable<List<WeatherPhoto>?> {
        return mILocalDataSource.getWeatherPhotoList()
    }

}