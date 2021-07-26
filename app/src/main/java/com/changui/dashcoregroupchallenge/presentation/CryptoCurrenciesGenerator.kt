package com.changui.dashcoregroupchallenge.presentation

import com.changui.dashcoregroupchallenge.domain.entity.CryptoCurrency
import javax.inject.Inject

class CryptoCurrenciesGenerator @Inject constructor (private val resourcesHelper: ResourcesHelper) {
    val cryptoCurrencies
        get() = resourcesHelper.cryptoCurrencyNames.zip(resourcesHelper.cryptoCurrencyCodes) {
            name, code -> CryptoCurrency(name, code)
        }
}