package com.tohami.photo_weather.data.model

import android.content.Context
import com.tohami.photo_weather.utils.getString
import java.util.*

class StringModel(private val target: Any?, private vararg val args: Any?) {

    fun getString(context: Context, locale: Locale): String? =
        target.getString(context, locale, *args)
}
