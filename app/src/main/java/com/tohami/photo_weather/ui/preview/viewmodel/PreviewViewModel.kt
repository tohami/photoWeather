package com.tohami.photo_weather.ui.preview.viewmodel

import com.tohami.photo_weather.data.model.ProgressTypes
import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.data.model.StatusCode
import com.tohami.photo_weather.R
import com.tohami.photo_weather.ui.base.BaseViewModel
import com.tohami.photo_weather.data.model.ErrorModel
import com.tohami.photo_weather.data.model.StringModel
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.data.model.dto.APIResponse
import com.tohami.photo_weather.data.model.dto.CurrentWeather
import com.tohami.photo_weather.ui.preview.repository.PreviewRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class PreviewViewModel(
    private val previewRepository: PreviewRepository,
    private val lat: Double,
    private val lang: Double,
    private val photoPath: String
) : BaseViewModel() {

    private val GET_WEATHER_TAG: String = "GET_WEATHER_TAG"
    private var mCurrentWeatherStatus: Status<CurrentWeather>? = null
    private var mCurrentWeatherObservable: BehaviorSubject<Status<CurrentWeather>> =
        BehaviorSubject.create()


    internal fun getCurrentWeather(): Observable<Status<CurrentWeather>> {
        return if (mCurrentWeatherStatus == null) {
            mCurrentWeatherObservable.mergeWith(getCurrentWeather(ProgressTypes.MAIN_PROGRESS))
        } else {
            updateCurrentWeather(mCurrentWeatherStatus!!)
            mCurrentWeatherObservable.hide()
        }
    }

    internal fun onRetryErrorBtnClick(): Completable {
        return getCurrentWeather(ProgressTypes.MAIN_PROGRESS)
    }

    private fun getCurrentWeather(progressType: ProgressTypes): Completable {

        previewRepository.cancelAPI(GET_WEATHER_TAG)
        return previewRepository.getCurrentWeather(GET_WEATHER_TAG, lat, lang)
            .doOnSubscribe { onGetCurrentWeatherSubscribe(progressType) }
            .doOnError { throwable -> onGetCurrentWeatherError(throwable, progressType) }
            .doOnSuccess { onGetCurrentWeatherSuccess(progressType) }
            .onErrorReturnItem(Status.Error(errorModel = ErrorModel.Error()))
            .map(this::mapCurrentWeatherResponse)
            .ignoreElement()
    }

    private fun mapCurrentWeatherResponse(currentWeatherListStatus: Status<APIResponse<CurrentWeather>>) {
        val status: Status<CurrentWeather> = when (validateResponse(currentWeatherListStatus)) {
            StatusCode.VALID ->
                onCurrentWeatherValid(currentWeatherListStatus)
            StatusCode.NOT_AUTHORIZED ->
                Status.NotAuthorized(errorModel = ErrorModel.NotAuthorized())
            StatusCode.NO_NETWORK ->
                Status.NoNetwork(errorModel = ErrorModel.NoNetworkError())
            StatusCode.ERROR ->
                Status.Error(errorModel = ErrorModel.Error())
            else ->
                Status.Error(errorModel = ErrorModel.Error())
        }

        setCurrentWeatherStatus(status)
        updateCurrentWeather(status)

    }

    private fun onCurrentWeatherValid(acurrentWeatherStatus: Status<APIResponse<CurrentWeather>>): Status<CurrentWeather> {
        val newCurrentWeather = acurrentWeatherStatus.data?.result
            ?: return Status.NoData(errorModel = ErrorModel.NoDataError(StringModel(R.string.no_results_found)))

        val currentList = mCurrentWeatherStatus?.data

        setCurrentWeatherStatus(Status.Success(currentList))

        return Status.Success(newCurrentWeather)
    }


    private fun updateCurrentWeather(status: Status<CurrentWeather>) {
        mCurrentWeatherObservable.onNext(status)
    }

    private fun setCurrentWeatherStatus(status: Status<CurrentWeather>) {
        mCurrentWeatherStatus = status
    }

    private fun onGetCurrentWeatherSubscribe(progressType: ProgressTypes) {
        showProgress(true, progressType)
        shouldShowError(false)
    }

    private fun onGetCurrentWeatherSuccess(progressType: ProgressTypes) {
        showProgress(false, progressType)
    }

    private fun onGetCurrentWeatherError(throwable: Throwable, progressType: ProgressTypes) {
        showProgress(false, progressType)
        throwable.printStackTrace()
    }

    override fun onCleared() {
        previewRepository.cancelAPIs()
        super.onCleared()
    }

    fun saveAndContinue(): Single<Boolean> {
        return previewRepository.saveWeatherPhoto(
            WeatherPhoto(
                mCurrentWeatherStatus?.data!!,
                photoPath
            )
        ).doOnError {
            showToastMessage("Unable to save data to database")
        }.andThen(Single.just(true))
    }
}
