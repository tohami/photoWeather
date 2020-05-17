package com.tohami.photo_weather.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun Any?.getString(context: Context, locale: Locale, vararg args: Any?): String? {
    return when (this) {
        is Int -> String.format(locale, context.getString(this), *args)
        is String -> String.format(locale, this, *args)
        else -> null
    }
}


fun Long.ToFormattedTime(): String {
    val date = Date(this * 1000)
    val format = SimpleDateFormat("dd MMM HH:mm a", Locale.getDefault())
    return format.format(date)
}

fun String.ToLongDateTime(): Long {
    val df = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
    return df.parse(this).time
}