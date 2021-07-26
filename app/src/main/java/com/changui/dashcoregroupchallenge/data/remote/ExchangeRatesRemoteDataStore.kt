package com.changui.dashcoregroupchallenge.data.remote

import com.changui.dashcoregroupchallenge.domain.ResultState
import javax.inject.Inject

interface ExchangeRatesRemoteDataStore {
    suspend fun fetchExchangeRates(cryptoCurrency: String): ResultState<ExchangeRatesApiResponse>
}

class ExchangeRatesRemoteDataStoreImpl @Inject constructor (private val apiService: BitPayApiService,
                                                            private val errorFactory: ExchangeRatesFailureFactory
)
    : ExchangeRatesRemoteDataStore {
    override suspend fun fetchExchangeRates(cryptoCurrency: String): ResultState<ExchangeRatesApiResponse> {
        return try {
            val apiResponse = apiService.getCryptoCurrencyExchangeRate(cryptoCurrency)
            ResultState.Success(apiResponse ?: ExchangeRatesApiResponse.EMPTY)
        } catch (e: Exception) {
            ResultState.Error(errorFactory.produce(e), null)
        }
    }
}

