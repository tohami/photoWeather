package com.tohami.photo_weather.data.remote.retrofit

import com.google.gson.GsonBuilder
import com.tohami.photo_weather.data.model.ApiStatus
import com.tohami.photo_weather.data.model.dto.APIResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.util.*


open class RetrofitConfigurations protected constructor() {

    private val mCalls = HashMap<String, Call<*>>()

    companion object {
        const val CANCELED_HTTP_CODE = 411
    }

    @Throws(Exception::class)
    protected fun <R> executeAPIResponseCall(call: Call<*>, tag: String): APIResponse<R> {
        val response = executeApiCall<R>(call, tag)
        return getApiResponse(response)
    }

    @Throws(Exception::class)
    protected fun <R> executeApiCall(call: Call<*>, tag: String): Response<R> {
        return try {
            mCalls[tag] = call
            val response: Response<R> = call.execute() as Response<R>
            mCalls.remove(tag)
            response
        } catch (e: IOException) {
            e.printStackTrace()
            if (call.isCanceled) {
                Response.error(CANCELED_HTTP_CODE, "".toResponseBody(null))
            } else
                throw (e)
        }
    }

    protected fun cancelRetrofitRequest(tag: String?) {
        val call = mCalls[tag]
        if (call != null) {
            call.cancel()
            mCalls.remove(tag)
        }
    }

    private fun <R> getApiResponse(response: Response<R>): APIResponse<R> {
        if (response.isSuccessful && response.body() != null) {
            response.body()!!
            return APIResponse(result = response.body(), httpCode = response.code())
        } else {
            return getErrorBody(response)
        }
    }

    private fun <R> getErrorBody(response: Response<R>): APIResponse<R> {
        return try {
            val gson = GsonBuilder().create()
            val responseString = response.errorBody()?.string()
            if (!responseString.isNullOrBlank())
                gson.fromJson<APIResponse<R>>(responseString, APIResponse::class.java)
            else
                APIResponse(httpCode = ApiStatus.STATUS_FAIL)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            APIResponse(httpCode = ApiStatus.STATUS_FAIL)
        }
    }
}
