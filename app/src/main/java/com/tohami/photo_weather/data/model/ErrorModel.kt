package com.tohami.photo_weather.data.model

import androidx.annotation.DrawableRes
import com.tohami.photo_weather.R

sealed class ErrorModel(
    val errorTitle: StringModel? = null, val errorSubTitle: StringModel? = null,
    @DrawableRes val errorIcon: Int? = null
) {

    class NoDataError(
        errorTitle: StringModel? = null,
        errorSubTitle: StringModel? = StringModel(R.string.no_results_found)
    ) : ErrorModel(errorTitle, errorSubTitle, R.drawable.ic_no_items)

    class NoNetworkError(
        errorTitle: StringModel? = StringModel(R.string.offline),
        errorSubTitle: StringModel? = StringModel(R.string.no_internet_connection)
    ) : ErrorModel(errorTitle, errorSubTitle, R.drawable.ic_offline)

    class Error(
        errorTitle: StringModel? = StringModel(R.string.oops_unknown_error),
        errorSubTitle: StringModel? = StringModel(R.string.something_went_wrong)
    ) : ErrorModel(errorTitle, errorSubTitle, R.drawable.ic_no_items)

    class NotAuthorized(
        errorTitle: StringModel? = StringModel(R.string.not_authorized),
        errorSubTitle: StringModel? = StringModel(R.string.not_authorized_subtitle)
    ) : ErrorModel(errorTitle, errorSubTitle, R.drawable.ic_no_items)
}
