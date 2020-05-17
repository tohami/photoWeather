package com.tohami.photo_weather.ui.preview.view

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.tohami.photo_weather.data.model.LoadingModel
import com.tohami.photo_weather.data.model.ProgressTypes
import com.tohami.photo_weather.data.model.Status
import com.tohami.photo_weather.data.model.StatusCode
import com.squareup.picasso.Picasso
import com.tohami.photo_weather.R
import com.tohami.photo_weather.ui.base.BaseFragment
import com.tohami.photo_weather.ui.base.IErrorViews
import com.tohami.photo_weather.data.model.dto.CurrentWeather
import com.tohami.photo_weather.ui.preview.viewmodel.PreviewViewModel
import com.tohami.photo_weather.utils.Constants
import com.tohami.photo_weather.utils.ToFormattedTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_weather_photo.*
import kotlinx.android.synthetic.main.view_error_layout.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import kotlin.math.roundToInt

class PreviewFragment : BaseFragment(),
    IErrorViews {

    companion object {
        fun getBundle(latitude: Double?, longitude: Double?, photoPath: String): Bundle {
            return bundleOf(
                Constants.Bundles.LATITUDE to latitude,
                Constants.Bundles.LONGITUDE to longitude,
                Constants.Bundles.PHOTO_PATH to photoPath
            )
        }
    }

    private val previewViewModel: PreviewViewModel by viewModel {
        parametersOf(
            arguments?.getDouble(Constants.Bundles.LATITUDE) ?: 0.0,
            arguments?.getDouble(Constants.Bundles.LONGITUDE) ?: 0.0,
            arguments?.getString(Constants.Bundles.PHOTO_PATH) ?: ""
        )
    }

    override val layoutID: Int
        get() = R.layout.fragment_weather_photo
    override val toolbarTitle: String
        get() = "New Photo"
    override val toolbarVisibility: Boolean
        get() = true

    override fun initViews(savedInstanceState: Bundle?) {
        Picasso.get().load(File(arguments?.getString(Constants.Bundles.PHOTO_PATH)))
            .into(weatherPhotoIv)

    }

    override fun setListeners() {
        saveAndContinueBtn.setOnClickListener {
            addDisposable(
                previewViewModel.saveAndContinue()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ this.onDataSavedToDb() }, this::onError)
            )
        }
    }

    private fun onDataSavedToDb() {
        navigationController?.popBackStack(R.id.homeFragment, false)
    }


    override fun bindViewModels() {
        addDisposable(bindGetCurrentWeatherData())
        addDisposable(bindErrorObserver())
        addDisposable(bindToastMessageObserver())
        addDisposable(bindLoadingObserver())
    }

    private fun bindGetCurrentWeatherData(): Disposable {
        return previewViewModel.getCurrentWeather()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ this.onWeatherPhotosRetrieved(it) }, this::onError)
    }

    private fun onWeatherPhotosRetrieved(currentWeather: Status<CurrentWeather>) {
        when (currentWeather.statusCode) {
            StatusCode.SUCCESS -> {
                onGetCurrentWeatherSuccess(currentWeather.data)
            }
            StatusCode.NO_DATA, StatusCode.ERROR, StatusCode.NO_NETWORK -> {
                onError(mContext, weatherDataContainer, layoutError, currentWeather.errorModel!!,
                    onRetryClick = View.OnClickListener { onRetryErrorBtnClick() })
            }
            else -> {
            }
        }
    }

    private fun onGetCurrentWeatherSuccess(currentWeather: CurrentWeather?) {
        onSuccess(weatherDataContainer, layoutError)

        Picasso.get().load(currentWeather?.weather?.get(0)?.iconUrl()).into(tempStatusIv)

        locationNameTv.text =
            String.format("%s, %s", currentWeather?.name, currentWeather?.sys?.country)
        dateTimeTv.text = currentWeather?.dt?.toLong()?.ToFormattedTime()
        tempStatusTv.text = currentWeather?.weather?.get(0)?.description
        currentTempTv.text = currentWeather?.main?.temp?.roundToInt().toString()
        currentTempUnit.text = "C"

    }

    private fun onRetryErrorBtnClick() {
        addDisposable(
            previewViewModel.onRetryErrorBtnClick()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        )
    }

    private fun bindLoadingObserver(): Disposable {
        return previewViewModel.loadingObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showProgress, this::onError)
    }

    private fun showProgress(loadingModel: LoadingModel) {
        when (loadingModel.progressType) {
            ProgressTypes.MAIN_PROGRESS -> {
                progressbar.visibility = if (loadingModel.shouldShow) View.VISIBLE else View.GONE
            }
            else -> {
            }
        }
    }

    private fun showError(shouldShow: Boolean) {
        shouldShowErrorLayout(layoutError, shouldShow)
    }


    private fun bindErrorObserver(): Disposable {
        return previewViewModel.errorViewObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showError, this::onError)
    }


    private fun bindToastMessageObserver(): Disposable {
        return previewViewModel.showToastObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::showMessage, this::onError)
    }
}