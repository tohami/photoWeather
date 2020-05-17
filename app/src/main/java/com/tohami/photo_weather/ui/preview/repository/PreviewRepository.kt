package com.tohami.photo_weather.ui.preview.repository

import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.ui.base.BaseRepository
import com.tohami.photo_weather.data.local.ILocalDataSource
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.data.model.dto.APIResponse
import com.tohami.photo_weather.data.model.dto.CurrentWeather
import com.tohami.photo_weather.data.remote.IRemoteDataSource
import com.tohami.photo_weather.utils.ConnectionUtils
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.Callable

class PreviewRepository(
    mIRemoteDataSource: IRemoteDataSource,
    mILocalDataSource: ILocalDataSource,
    connectionUtils: ConnectionUtils
) : BaseRepository(mIRemoteDataSource, mILocalDataSource, connectionUtils) {

    fun getCurrentWeather(
        tag: String,
        lat: Double,
        lang: Double
    ): Single<Status<APIResponse<CurrentWeather>>> {
        return if (isConnected()) {
            createSingle(tag, Callable<Status<APIResponse<CurrentWeather>>> {
                Status.Success(mIRemoteDataSource.getCurrentWeatherData(lat, lang, tag))
            })
        } else {
            createSingle(tag, Callable<Status<APIResponse<CurrentWeather>>> {
                Status.NoNetwork()
            })
        }
    }

    fun saveWeatherPhoto(weatherPhoto: WeatherPhoto): Completable {
        return mILocalDataSource.saveWeatherPhoto(weatherPhoto)
    }
}