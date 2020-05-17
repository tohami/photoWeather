package com.tohami.photo_weather.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.tohami.photo_weather.ui.camera.view.CameraInteractionListener
import com.tohami.photo_weather.ui.camera.view.UiProvider
import com.tohami.photo_weather.ui.camera.view.CameraFragment
import com.tohami.photo_weather.utils.FileUtils.createImageFile
import com.tohami.photo_weather.utils.FileUtils.createImageGallery
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CustomCameraManager(
    private val context: Context,
    private val lifecycle: Lifecycle,
    private val uiProvider: UiProvider,
    private val cameraInteractionListener: CameraInteractionListener
) : LifecycleObserver {
    private val cameraManager: CameraManager
    private val cameraFacing: Int
    private var surfaceTextureListener: SurfaceTextureListener? = null
    private var previewSize: Size? = null
    private var mCameraId: String? = null
    private var backgroundThread: HandlerThread? = null
    private var backgroundHandler: Handler? = null
    private var stateCallback: CameraDevice.StateCallback? = null
    private var mCameraDevice: CameraDevice? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private var captureRequest: CaptureRequest? = null
    private var mCameraCaptureSession: CameraCaptureSession? = null
    private lateinit var imageFile: File
    private fun setCameraStateCallback() {
        stateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(cameraDevice: CameraDevice) {
                mCameraDevice = cameraDevice
                createPreviewSession()
            }

            override fun onDisconnected(cameraDevice: CameraDevice) {
                cameraDevice.close()
                mCameraDevice = null
            }

            override fun onError(cameraDevice: CameraDevice, error: Int) {
                cameraDevice.close()
                mCameraDevice = null
            }
        }
    }

    private fun setTextureListener() {
        surfaceTextureListener = object : SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
                setUpCamera()
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(
                surfaceTexture: SurfaceTexture,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) { // no impl needed
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        try {
            cameraManager.openCamera(mCameraId!!, stateCallback!!, backgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openBackgroundThread() {
        backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread!!.start()
        backgroundHandler = Handler(backgroundThread!!.looper)
    }

    private fun createPreviewSession() {
        try {
            val surfaceTexture = uiProvider.textureView().surfaceTexture
            surfaceTexture.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
            val previewSurface = Surface(surfaceTexture)
            captureRequestBuilder =
                mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder!!.addTarget(previewSurface)
            mCameraDevice!!.createCaptureSession(
                listOf(previewSurface),
                object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        if (mCameraDevice == null) {
                            return
                        }
                        try {
                            captureRequest = captureRequestBuilder!!.build()
                            mCameraCaptureSession = cameraCaptureSession
                            mCameraCaptureSession!!.setRepeatingRequest(
                                captureRequest!!,
                                null, backgroundHandler
                            )
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) { // no impl needed
                    }
                }, backgroundHandler
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun setUpCamera() {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val cameraCharacteristics =
                    cameraManager.getCameraCharacteristics(cameraId)
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                    cameraFacing
                ) {
                    val streamConfigurationMap =
                        cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                        )
                    if (streamConfigurationMap != null) {
                        previewSize = streamConfigurationMap.getOutputSizes(
                            SurfaceTexture::class.java
                        )[0]
                        mCameraId = cameraId
                    }
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    fun onPhotoCaptureClicked() {
        var outputPhoto: FileOutputStream? = null
        var errorMessage: String? = null
        try {
            imageFile = createImageFile(
                createImageGallery(
                    context,
                    "Pure"
                )
            )
            outputPhoto = FileOutputStream(imageFile)
            uiProvider.textureView().bitmap
                .compress(Bitmap.CompressFormat.PNG, 100, outputPhoto)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
            e.printStackTrace()
        } finally {
            try {
                outputPhoto?.close()
            } catch (e: IOException) {
                e.printStackTrace()
                errorMessage = e.localizedMessage
            }
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                animateShutter()
                cameraInteractionListener.onPhotoCaptureSuccess(imageFile)
            } else {
                cameraInteractionListener.onPhotoCaptureFailure(errorMessage)
            }
        }
    }

    private fun animateShutter() {
        uiProvider.shutterView().visibility = View.VISIBLE
        uiProvider.shutterView().alpha = 0f
        val alphaInAnim =
            ObjectAnimator.ofFloat(uiProvider.shutterView(), "alpha", 0f, 0.8f)
        alphaInAnim.duration = 100
        alphaInAnim.startDelay = 100
        alphaInAnim.interpolator =
            ACCELERATE_INTERPOLATOR
        val alphaOutAnim =
            ObjectAnimator.ofFloat(uiProvider.shutterView(), "alpha", 0.8f, 0f)
        alphaOutAnim.duration = 200
        alphaOutAnim.interpolator =
            DECELERATE_INTERPOLATOR
        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(alphaInAnim, alphaOutAnim)
        animatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                uiProvider.shutterView().visibility = View.GONE
            }
        })
        animatorSet.start()
    }

    private fun closeCamera() {
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession!!.close()
            mCameraCaptureSession = null
        }
        if (mCameraDevice != null) {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
    }

    private fun closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread!!.quitSafely()
            backgroundThread = null
            backgroundHandler = null
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        openBackgroundThread()
        if (uiProvider.textureView().isAvailable) {
            setUpCamera()
            openCamera()
        } else {
            uiProvider.textureView().surfaceTextureListener = surfaceTextureListener
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        closeCamera()
        closeBackgroundThread()
    }

    companion object {
        private val ACCELERATE_INTERPOLATOR: Interpolator =
            AccelerateInterpolator()
        private val DECELERATE_INTERPOLATOR: Interpolator =
            DecelerateInterpolator()
        private val TAG = CameraFragment::class.java.simpleName
    }

    init {
        cameraManager =
            context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        setTextureListener()
        setCameraStateCallback()
    }
}