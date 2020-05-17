package com.tohami.photo_weather.ui.share.view

import android.content.Intent
import android.os.Bundle
import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.transition.TransitionInflater
import com.squareup.picasso.Picasso
import com.tohami.photo_weather.R
import com.tohami.photo_weather.ui.base.BaseFragment
import com.tohami.photo_weather.data.model.WeatherPhoto
import com.tohami.photo_weather.utils.Constants
import com.tohami.photo_weather.utils.FileUtils
import com.tohami.photo_weather.utils.ToFormattedTime
import kotlinx.android.synthetic.main.fragment_share.*
import java.io.File
import kotlin.math.roundToInt

class ShareFragment : BaseFragment() {

    companion object {
        fun getBundle(weatherPhoto: WeatherPhoto): Bundle {
            return bundleOf(
                Constants.Bundles.WEATHER_PHOTO to weatherPhoto
            )
        }
    }

    private var observer: FileObserver? = null
    private lateinit var currentWeather: WeatherPhoto
    override val layoutID: Int
        get() = R.layout.fragment_share
    override val toolbarTitle: String
        get() = "Share Photo"
    override val toolbarVisibility: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentWeather =
            arguments?.getParcelable<WeatherPhoto>(Constants.Bundles.WEATHER_PHOTO) as WeatherPhoto

        weatherPhotoIv.transitionName = "weatherPhotoIv" + currentWeather.photoPath
        currentTempTv.transitionName = "currentTempTv" + currentWeather.photoPath
        locationNameTv.transitionName = "locationNameTv" + currentWeather.photoPath
    }
    override fun initViews(savedInstanceState: Bundle?) {
        currentWeather =
            arguments?.getParcelable<WeatherPhoto>(Constants.Bundles.WEATHER_PHOTO) as WeatherPhoto

        Picasso.get().load(File(currentWeather.photoPath))
            .into(weatherPhotoIv)

        Picasso.get().load(currentWeather.currentWeather.weather?.get(0)?.iconUrl())
            .into(tempStatusIv)

        locationNameTv.text =
            String.format(
                "%s, %s",
                currentWeather.currentWeather.name,
                currentWeather.currentWeather.sys?.country
            )
        dateTimeTv.text = currentWeather.currentWeather.dt?.toLong()?.ToFormattedTime()
        tempStatusTv.text = currentWeather.currentWeather.weather?.get(0)?.description
        currentTempTv.text = currentWeather.currentWeather.main?.temp?.roundToInt().toString()
        currentTempUnit.text = "C"


    }

    override fun setListeners() {
        shareBtn.setOnClickListener {
            generateAndShareImage()
        }
    }

    override fun bindViewModels() {
    }

    fun shareFile(file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        val myPhotoFileUri = FileProvider.getUriForFile(
            requireActivity(), requireActivity().applicationContext.packageName
                    + ".provider", file
        )
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri)
        startActivity(Intent.createChooser(intent, "Share Using..."))
    }

    private fun generateAndShareImage() {
        val filename = File(currentWeather.photoPath).nameWithoutExtension

        val imageFile = FileUtils.createImageFile(
            FileUtils.createImageGallery(requireContext(), "Shared"),
            filename
        )

        val bitmap = FileUtils.convertViewToBitmap(weatherPhotoContainer)
        observeFileChanges(imageFile)
        FileUtils.saveBitmapToFile(bitmap, imageFile)

    }


    private fun observeFileChanges(photo: File) {

        observer = object : FileObserver(photo.path) {
            override fun onEvent(event: Int, file: String?) {
                Handler(Looper.getMainLooper()).post {
                    if (event == CLOSE_WRITE) {
                        shareFile(photo)
                    }
                }
            }
        }
        observer?.startWatching()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        observer?.stopWatching()
    }
}