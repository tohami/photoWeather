package com.tohami.photo_weather.data.model.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tohami.photo_weather.data.model.ApiStatus

data class APIResponse<T>(
    @SerializedName("Result")
    @Expose
    var result: T? = null,
    @SerializedName("Message")
    @Expose
    var serverMessage: String? = null,
    var httpCode: Int = ApiStatus.NO_HTTP_CODE
)