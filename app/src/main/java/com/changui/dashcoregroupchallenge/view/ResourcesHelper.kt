package com.changui.dashcoregroupchallenge.view

import android.content.Context
import com.changui.dashcoregroupchallenge.R
import javax.inject.Inject

/*
* The sole purpose of the class is to return string/array resources in non-view classes, thereby abstracting the dependence on the system.
 */

class ResourcesHelper @Inject constructor(private val applicationContext: Context) {
    val networkErrorMessage
        get() = applicationContext.getString(R.string.error_network_desc)

    val serverErrorMessage
        get() = applicationContext.getString(R.string.error_internal_server_desc)

    val badRequestErrorMessage
        get() = applicationContext.getString(R.string.error_forbidden_desc)

    val gatewayErrorMessage
        get() = applicationContext.getString(R.string.error_gateway_desc)

    val unknownErrorMessage
        get() = applicationContext.getString(R.string.error_unknown_desc)

    val cryptoCurrencyNames: Array<String>
        get() = applicationContext.resources.getStringArray(R.array.crypto_currency_names)

    val cryptoCurrencyCodes: Array<String>
        get() = applicationContext.resources.getStringArray(R.array.crypto_currency_codes)

}