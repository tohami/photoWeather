package com.tohami.photo_weather.ui.base

import androidx.annotation.CallSuper
import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.data.local.ILocalDataSource
import com.tohami.photo_weather.data.model.dto.APIResponse
import com.tohami.photo_weather.data.remote.IRemoteDataSource
import com.tohami.photo_weather.data.remote.retrofit.RetrofitConfigurations
import com.tohami.photo_weather.utils.ConnectionUtils
import io.reactivex.Single
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

abstract class BaseRepository(
    val mIRemoteDataSource: IRemoteDataSource,
    val mILocalDataSource: ILocalDataSource,
    private val connectionUtils: ConnectionUtils
) {

    private val tagsList = ArrayList<String>()

    protected fun isConnected(): Boolean {
        return connectionUtils.isConnected
    }

    protected fun <S> createSingle(tag: String, callable: Callable<Status<S>>?): Single<Status<S>> {
        addTag(getTimestampedTag(tag))
        return Single.fromCallable(callable)
            .map {
                removeTag(tag)

                return@map if (it.data is APIResponse<*>? &&
                    it.data?.httpCode == RetrofitConfigurations.CANCELED_HTTP_CODE
                )
                    Status.Idle()
                else
                    it
            }
    }


    private fun getTimestampedTag(tag: String): String {
        return tag + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
    }

    private fun removeTag(tag: String) {
        tagsList.remove(tag)
    }

    private fun addTag(tag: String) {
        if (!tagsList.contains(tag))
            tagsList.add(tag)
    }

    open fun cancelAPI(tagPrefix: String) {
        val filteredList = tagsList.filter { it.startsWith(tagPrefix) }
        for (tag in filteredList) {
            removeTag(tag)
            mIRemoteDataSource.cancelRequest(tag)
        }
    }

    @CallSuper
    open fun cancelAPIs() {
        for (tag in tagsList)
            mIRemoteDataSource.cancelRequest(tag)
    }
}
