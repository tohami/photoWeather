package com.tohami.photo_weather.ui.home.viewmodel

import com.tohami.photo_weather.data.model.ProgressTypes
import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.ui.base.BaseViewModel
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.ui.home.repository.HomeRepository
import io.reactivex.Observable

class HomeViewModel(private val repository: HomeRepository) : BaseViewModel() {
    fun getWeatherPhotosList(): Observable<Status<List<WeatherPhoto>>?> {
        return repository.weatherPhotoList()
            .doOnSubscribe { onGetWeatherPhotosSubscribe() }
            .doOnError { throwable -> onGetWeatherPhotosError(throwable) }
            .onErrorReturnItem(emptyList())
            .map {
                if (it.isNullOrEmpty())
                    return@map Status.NoData<List<WeatherPhoto>>()
                else
                    return@map Status.Success(data = it)
            }
    }

    private fun onGetWeatherPhotosSubscribe() {
        shouldShowError(false)
    }

    private fun onGetWeatherPhotosError(throwable: Throwable) {
        showProgress(false, ProgressTypes.MAIN_PROGRESS)
    }

    override fun onCleared() {
        repository.cancelAPIs()
        super.onCleared()
    }
}