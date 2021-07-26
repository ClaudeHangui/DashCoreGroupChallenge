package com.changui.dashcoregroupchallenge.data.mapper

import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRate
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesApiResponse
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase

interface Mapper<in I1, in I2,  out Output> {
    fun map(input1: I1, inputI2: I2): Output
}

class RemoteToLocalMapper: Mapper<ExchangeRatesApiResponse, GetExchangeRatesUseCase.GetExchangeRatesParams, CryptoCurrencyExchangeRate> {
    override fun map(input1: ExchangeRatesApiResponse, inputI2: GetExchangeRatesUseCase.GetExchangeRatesParams): CryptoCurrencyExchangeRate {
        return CryptoCurrencyExchangeRate(
            inputI2.cryptoCurrencyName,
            inputI2.cryptoCurrencyCode,
            input1.data ?: emptyList()
        )
    }
}