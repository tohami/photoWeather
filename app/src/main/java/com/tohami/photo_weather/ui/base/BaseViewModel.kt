package com.tohami.photo_weather.ui.base

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.tohami.photo_weather.data.model.LoadingModel
import com.tohami.photo_weather.data.model.ProgressTypes
import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.data.model.StatusCode
import com.tohami.photo_weather.data.model.StringModel
import com.tohami.photo_weather.data.model.dto.APIResponse
import io.reactivex.subjects.BehaviorSubject
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel : ViewModel() {

    val loadingObservable: BehaviorSubject<LoadingModel> = BehaviorSubject.create()
    val errorViewObservable: BehaviorSubject<Boolean> = BehaviorSubject.create()
    val showToastObservable: BehaviorSubject<StringModel> = BehaviorSubject.create()

    protected fun showProgress(
        shouldShow: Boolean,
        progressType: ProgressTypes = ProgressTypes.MAIN_PROGRESS
    ) {
        loadingObservable.onNext(LoadingModel(shouldShow, progressType))
    }

    protected fun shouldShowError(shouldShow: Boolean) {
        errorViewObservable.onNext(shouldShow)
    }

    protected fun showToastMessage(message: Any?, vararg args: Any?) {
        if (message is StringModel)
            showToastObservable.onNext(message)
        else
            showToastObservable.onNext(StringModel(message, *args))
    }

    @CallSuper
    protected fun validateResponse(statusResponse: Status<*>?): StatusCode {
        if (statusResponse == null)
            return StatusCode.ERROR

        if (statusResponse.isOfflineData())
            return StatusCode.OFFLINE_DATA

        if (statusResponse.isIdle())
            return StatusCode.IDLE

        if (statusResponse.isNoNetwork())
            return StatusCode.NO_NETWORK

        if (statusResponse.isError())
            return StatusCode.ERROR

        if (statusResponse.data == null)
            return StatusCode.ERROR

        if (statusResponse.data is APIResponse<*> && statusResponse.data.httpCode == HttpsURLConnection.HTTP_UNAUTHORIZED)
            return StatusCode.NOT_AUTHORIZED

        return StatusCode.VALID
    }
}
