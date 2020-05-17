package com.tohami.photo_weather.ui.preview.view

import android.location.Location

interface LocationManagerInteraction {
    fun onLocationRetrieved(location: Location?)
}