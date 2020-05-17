package com.tohami.photo_weather.ui.camera.view

import java.io.File

interface CameraInteractionListener {
    fun onPhotoCaptureSuccess(imageFile: File)
    fun onPhotoCaptureFailure(errorMessage: String?)
}