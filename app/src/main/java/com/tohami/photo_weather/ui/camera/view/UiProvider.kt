package com.tohami.photo_weather.ui.camera.view

import android.view.TextureView
import android.view.View

interface UiProvider {
    fun textureView(): TextureView
    fun shutterView(): View
}