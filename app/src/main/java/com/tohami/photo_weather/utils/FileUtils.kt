package com.tohami.photo_weather.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import com.tohami.photo_weather.R
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    private const val IMAGE_FORMAT = ".jpg"

    @JvmStatic
    fun createImageGallery(context: Context, galleryName: String): File {
        val storageDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val galleryFolder = File(
            storageDirectory,
            context.resources.getString(R.string.app_name) + "/" + galleryName
        )
        if (!galleryFolder.exists()) {
            val wasCreated = galleryFolder.mkdirs()
            if (!wasCreated) {
                Log.e("CapturedImages", "Failed to create directory")
            }
        }
        return galleryFolder
    }

    @JvmStatic
    @Throws(IOException::class)
    fun createImageFile(galleryFolder: File, imageName: String? = null): File {
        val imageFileName = if (imageName == null) {
            val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(Date())

            "image_" + timeStamp + "_"
        } else {
            imageName
        }
        return File.createTempFile(
            imageFileName,
            IMAGE_FORMAT,
            galleryFolder
        )
    }

    @JvmStatic
    fun convertViewToBitmap(v: ConstraintLayout): Bitmap {
        val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.RGB_565)
        val c = Canvas(b)
        v.draw(c)
        return b
    }

    @JvmStatic
    fun saveBitmapToFile(
        bitmap: Bitmap,
        imageFile: File
    ) {
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(imageFile)
                newBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.flush()
                        outputStream.fd.sync()
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}
