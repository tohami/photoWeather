package com.tohami.photo_weather.ui.base

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tohami.photo_weather.R
import com.tohami.photo_weather.data.model.ErrorModel
import java.util.*


interface IErrorViews {

    fun onError(
        context: Context,
        viewToHide: View? = null,
        errorView: View,
        errorModel: ErrorModel,
        onRetryClick: View.OnClickListener?
    ) {
        val txtErrorTitle = errorView.findViewById<TextView>(R.id.txtErrorTitle)
        val txtErrorSubTitle = errorView.findViewById<TextView>(R.id.txtErrorSubTitle)
        val btnError = errorView.findViewById<Button>(R.id.btnError)
        val imgError = errorView.findViewById<ImageView>(R.id.imgError)

        if (viewToHide != null) viewToHide.visibility = View.GONE
        errorView.visibility = View.VISIBLE

        val errorTitle = errorModel.errorTitle?.getString(context, Locale.getDefault())
        val errorSubTitle = errorModel.errorSubTitle?.getString(context, Locale.getDefault())
        val errorIcon = errorModel.errorIcon

        if (!errorTitle.isNullOrBlank()) {
            txtErrorTitle.text = errorTitle
            txtErrorTitle.visibility = View.VISIBLE
        } else txtErrorTitle.visibility = View.GONE

        if (!errorSubTitle.isNullOrBlank()) {
            txtErrorSubTitle.text = errorSubTitle
            txtErrorSubTitle.visibility = View.VISIBLE
        } else txtErrorSubTitle.visibility = View.GONE

        if (onRetryClick != null) {
            btnError.visibility = View.VISIBLE
            btnError.setOnClickListener(onRetryClick)
        } else btnError.visibility = View.GONE

        if (errorIcon != null) {
            imgError.visibility = View.VISIBLE
            imgError.setBackgroundResource(errorIcon)
        } else imgError.visibility = View.GONE
    }

    fun onSuccess(viewToShow: View? = null, errorView: View) {
        val viewErrorLayout = errorView.findViewById<LinearLayout>(R.id.layoutError)
        if (viewToShow != null) viewToShow.visibility = View.VISIBLE
        if (viewErrorLayout != null) viewErrorLayout.visibility = View.GONE
    }

    fun shouldShowErrorLayout(errorView: View, shouldShow: Boolean) {
        val viewErrorLayout = errorView.findViewById<LinearLayout>(R.id.layoutError)
        viewErrorLayout.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }
}
