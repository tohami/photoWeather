package com.tohami.photo_weather.ui.camera.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.tohami.photo_weather.R
import com.tohami.photo_weather.ui.base.BaseFragment
import com.tohami.photo_weather.utils.CustomCameraManager
import com.tohami.photo_weather.ui.preview.view.PreviewFragment
import com.tohami.photo_weather.utils.Constants
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File

class CameraFragment : BaseFragment(),
    UiProvider,
    CameraInteractionListener {

    companion object {
        fun getBundle(latitude: Double, longitude: Double): Bundle {
            return bundleOf(
                Constants.Bundles.LATITUDE to latitude,
                Constants.Bundles.LONGITUDE to longitude
            )
        }
    }

    override val layoutID: Int
        get() = R.layout.fragment_camera
    override val toolbarTitle: String
        get() = "Take Photo"
    override val toolbarVisibility: Boolean
        get() = false

    override fun initViews(savedInstanceState: Bundle?) {
        customCameraManager = CustomCameraManager(
            requireContext(),
            lifecycle,
            this,
            this
        )
        lifecycle.addObserver(customCameraManager!!)
    }

    override fun setListeners() {
        newWeatherPhotoBtn.setOnClickListener {
            customCameraManager!!.onPhotoCaptureClicked()
        }
    }

    override fun bindViewModels() {
    }

    private var customCameraManager: CustomCameraManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }


    override fun textureView(): TextureView {
        return textureViewId
    }

    override fun shutterView(): View {
        return mShutter
    }

    override fun onPhotoCaptureSuccess(imageFile: File) {
        val bundle = PreviewFragment.getBundle(
            arguments?.getDouble(Constants.Bundles.LATITUDE),
            arguments?.getDouble(Constants.Bundles.LONGITUDE),
            imageFile.path
        )
        navigationController?.navigate(R.id.action_cameraFragment_to_weatherFragment, bundle)
    }

    override fun onPhotoCaptureFailure(errorMessage: String?) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            Toast.makeText(activity, "Unable to Capture photo", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}