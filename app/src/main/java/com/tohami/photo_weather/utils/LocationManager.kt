package com.tohami.photo_weather.utils

import android.Manifest
import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.tohami.photo_weather.ui.preview.view.LocationManagerInteraction

class LocationManager @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION) constructor(
    private val activity: Activity,
    private val locationManagerInteraction: LocationManagerInteraction
) {

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mSettingsClient: SettingsClient? = null

    private var mLocationRequest: LocationRequest? = null

    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    private var mLocationCallback: LocationCallback? = null

    private fun setupLocationService() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        mSettingsClient = LocationServices.getSettingsClient(activity)
        createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationManagerInteraction.onLocationRetrieved(locationResult.lastLocation)
            }
        }
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
         mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    fun startLocationUpdates() { // Begin by checking if the device has the necessary location settings.
        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener(
                activity
            ) {
                Log.d(
                    TAG,
                    "All location settings are satisfied."
                )
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback, Looper.myLooper()
                )
            }
            .addOnFailureListener(activity) { e: Exception ->
                val statusCode = (e as ApiException).statusCode
                handleStartLocationFailureCases(e as ResolvableApiException, statusCode)
            }
    }

    private fun handleStartLocationFailureCases(
        e: ResolvableApiException,
        statusCode: Int
    ) {
        when (statusCode) {
            CommonStatusCodes.RESOLUTION_REQUIRED -> {
                Log.d(
                    TAG,
                    "Location settings are not satisfied. Attempting to upgrade " +
                            "location settings "
                )
                try {
                    e.startResolutionForResult(
                        activity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sie: SendIntentException) {
                    Log.d(
                        TAG,
                        "PendingIntent unable to execute request."
                    )
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                val errorMessage =
                    "Location settings are inadequate, and cannot be " +
                            "fixed here. Fix in Settings."
                Log.e(
                    TAG,
                    errorMessage
                )
            }
            else -> {
            }
        }
    }


    fun stopLocationUpdates() {
        mFusedLocationClient!!.removeLocationUpdates(mLocationCallback)
    }

    companion object {
        private const val TAG = "LocationManager"

        const val REQUEST_CHECK_SETTINGS = 2

        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000

        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    init {
        setupLocationService()
    }
}