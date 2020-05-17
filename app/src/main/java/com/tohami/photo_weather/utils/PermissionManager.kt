package com.tohami.photo_weather.utils

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.tohami.photo_weather.R
import java.util.*

object PermissionManager {
    const val LOCATION_PERMISSION_REQUEST_CODE = 110
    const val MULTIPLE_PERMISSION_REQUEST_CODE = 100
    private var permissionDialog: AlertDialog? = null
    @JvmStatic
    fun checkForPermission(fragment: Fragment, @PermissionName permission: String, @RequestCode requestCode: Int) {
        val permissionsNeeded =
            ArrayList<String>()
        if (ActivityCompat.checkSelfPermission(fragment.requireActivity(), permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(permission)
        }
        if (!permissionsNeeded.isEmpty()) {
            requestPermission(
                fragment,
                permissionsNeeded.toTypedArray(),
                requestCode
            )
        }
    }

    @JvmStatic
    fun isPermissionGranted(fragment: Fragment, @PermissionName permission: String?): Boolean {
        return (ActivityCompat.checkSelfPermission(fragment.requireActivity(), permission!!)
                == PackageManager.PERMISSION_GRANTED)
    }

    fun isAllPermissionGranted(
        fragment: Fragment,
        permissions: Array<String>
    ): Boolean {
        var isAllPermissionsGranted = true
        for (permission in permissions) {
            if (!isPermissionGranted(fragment, permission)) {
                isAllPermissionsGranted = false
                break
            }
        }
        return isAllPermissionsGranted
    }

    fun checkForPermissions(
        fragment: Fragment,
        permissions: Array<String>
    ) {
        val permissionsNeeded =
            ArrayList<String>()
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(fragment.requireActivity(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(permission)
            }
        }
        if (permissionsNeeded.isNotEmpty()) {
            requestPermission(
                fragment,
                permissionsNeeded.toTypedArray(),
                MULTIPLE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestPermission(
        fragment: Fragment,
        permissions: Array<String>, @RequestCode requestCode: Int
    ) {
        fragment.requestPermissions(permissions, requestCode)
    }

    @JvmStatic
    fun showApplicationSettingsDialog(context: Context) {
        if (permissionDialog == null) {
            val builder =
                AlertDialog.Builder(context, R.style.AlertDialogStyle)
            builder.setTitle(context.getString(R.string.permission_dialog_title))
                .setMessage(context.getString(R.string.msg_permission_required))
                .setPositiveButton(
                    context.getString(R.string.open_settings)
                ) { _: DialogInterface?, _: Int ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri =
                        Uri.fromParts("package", context.packageName, null)
                    intent.data = uri
                    context.startActivity(intent)
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            permissionDialog = builder.create()
            permissionDialog!!.setCanceledOnTouchOutside(false)
        }
        if (!permissionDialog!!.isShowing) {
            permissionDialog!!.show()
            permissionDialog!!.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(context.resources.getColor(R.color.colorText))
            permissionDialog!!.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(context.resources.getColor(R.color.colorAccent))
        }
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(
        LOCATION_PERMISSION_REQUEST_CODE,
        MULTIPLE_PERMISSION_REQUEST_CODE
    )
    annotation class RequestCode

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @StringDef(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    annotation class PermissionName
}