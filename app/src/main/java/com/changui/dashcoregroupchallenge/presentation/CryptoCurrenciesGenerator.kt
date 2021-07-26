package com.changui.dashcoregroupchallenge.presentation

import com.changui.dashcoregroupchallenge.domain.entity.CryptoCurrency
import com.changui.dashcoregroupchallenge.view.ResourcesHelper
import javax.inject.Inject

/**
 * class whose sole purpose is to generate a list of crypto currencies
 * the data being provided is from the string.xml file
 */
class CryptoCurrenciesGenerator @Inject constructor (private val resourcesHelper: ResourcesHelper) {
    val cryptoCurrencies
        get() = resourcesHelper.cryptoCurrencyNames.zip(resourcesHelper.cryptoCurrencyCodes) {
            name, code -> CryptoCurrency(name, code)
        }
}