package com.tohami.photo_weather.ui.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.FragmentNavigator
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohami.photo_weather.data.model.LoadingModel
import com.tohami.photo_weather.data.model.ProgressTypes
import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.data.model.StatusCode
import com.tohami.photo_weather.R
import com.tohami.photo_weather.ui.base.BaseFragment
import com.tohami.photo_weather.ui.base.IErrorViews
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.ui.camera.view.CameraFragment
import com.tohami.photo_weather.ui.home.viewmodel.HomeViewModel
import com.tohami.photo_weather.ui.share.view.ShareFragment
import com.tohami.photo_weather.utils.LocationManager
import com.tohami.photo_weather.ui.preview.view.LocationManagerInteraction
import com.tohami.photo_weather.utils.PermissionManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.view_error_layout.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment(),
    OnWeatherPhotoClickListener, IErrorViews,
    LocationManagerInteraction {
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var weatherPhotosAdapter: WeatherPhotoAdapter
    private var locationManager: LocationManager? = null

    override val layoutID: Int
        get() = R.layout.fragment_home
    override val toolbarTitle: String
        get() = "Home"
    override val toolbarVisibility: Boolean
        get() = true

    override fun initViews(savedInstanceState: Bundle?) {
        weatherPhotosAdapter =
            WeatherPhotoAdapter(
                ArrayList(),
                this
            )
        weatherRecyclerView.layoutManager = LinearLayoutManager(activity)
        weatherRecyclerView.adapter = weatherPhotosAdapter
    }

    override fun setListeners() {
        newWeatherPhotoBtn.setOnClickListener {
            onNewPhotoClicked()
        }
    }

    override fun bindViewModels() {
        addDisposable(bindGetWeatherPhotosList())
        addDisposable(bindErrorObserver())
        addDisposable(bindToastMessageObserver())
        addDisposable(bindLoadingObserver())
    }

    private fun bindGetWeatherPhotosList(): Disposable {
        return homeViewModel.getWeatherPhotosList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ this.onWeatherPhotosRetrieved(it) }, this::onError)
    }

    private fun onWeatherPhotosRetrieved(photos: Status<List<WeatherPhoto>>?) {
        when (photos?.statusCode) {
            StatusCode.SUCCESS -> {
                onActivitiesListSuccess(photos)
            }
            StatusCode.NO_DATA -> {
                emptyLayout.visibility = View.VISIBLE
            }
            StatusCode.ERROR, StatusCode.NO_NETWORK -> {
                onError(mContext, weatherRecyclerView, layoutError, photos.errorModel!!,
                    onRetryClick = View.OnClickListener { onRetryErrorBtnClick() })
            }
            else -> {
            }
        }
    }

    private fun onActivitiesListSuccess(photos: Status<List<WeatherPhoto>>) {
        onSuccess(weatherRecyclerView, layoutError)
        weatherPhotosAdapter.setItems(photos.data ?: ArrayList())
    }

    private fun onRetryErrorBtnClick() {
        homeViewModel.getWeatherPhotosList()
    }

    private fun bindLoadingObserver(): Disposable {
        return homeViewModel.loadingObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showProgress, this::onError)
    }

    private fun showProgress(loadingModel: LoadingModel) {
        when (loadingModel.progressType) {
            ProgressTypes.MAIN_PROGRESS ->
                progressbar.visibility = if (loadingModel.shouldShow) View.VISIBLE else View.GONE
            else -> {
            }
        }
    }

    private fun showError(shouldShow: Boolean) {
        shouldShowErrorLayout(layoutError, shouldShow)
    }


    private fun bindErrorObserver(): Disposable {
        return homeViewModel.errorViewObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showError, this::onError)
    }


    private fun bindToastMessageObserver(): Disposable {
        return homeViewModel.showToastObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showMessage, this::onError)
    }

    override fun onWeatherPhotoClick(
        weatherPhoto: WeatherPhoto,
        extras: FragmentNavigator.Extras
    ) {
        val bundle = ShareFragment.getBundle(weatherPhoto)
        navigationController?.navigate(R.id.action_homeFragment_to_shareFragment, bundle , null , extras)
    }


    @SuppressLint("MissingPermission")
    fun onNewPhotoClicked() {
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (PermissionManager.isAllPermissionGranted(this, permissions)) {
            locationManager = LocationManager(
                requireActivity(),
                this
            )
            showProgress(LoadingModel(true, ProgressTypes.MAIN_PROGRESS))
            locationManager?.startLocationUpdates()
        } else {
            PermissionManager.checkForPermissions(this, permissions)
        }
    }


    override fun onStop() {
        super.onStop()
        locationManager?.stopLocationUpdates()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionManager.MULTIPLE_PERMISSION_REQUEST_CODE) { // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                onNewPhotoClicked()
            } else {
                PermissionManager.showApplicationSettingsDialog(mContext)
            }
        }
    }


    override fun onLocationRetrieved(location: Location?) {
        Log.e("location", "has been find")
        location?.let {
            locationManager?.stopLocationUpdates()
            val bundle = CameraFragment.getBundle(location.latitude, location.longitude)
            navigationController?.navigate(R.id.action_homeFragment_to_cameraFragment, bundle)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LocationManager.REQUEST_CHECK_SETTINGS) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    locationManager?.startLocationUpdates()
                }
                Activity.RESULT_CANCELED -> {
                }
                else -> {
                }
            }
        }
    }
}