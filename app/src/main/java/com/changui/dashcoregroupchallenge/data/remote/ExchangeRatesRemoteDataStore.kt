package com.changui.dashcoregroupchallenge.data.remote

import arrow.core.Either
import com.changui.dashcoregroupchallenge.data.error.Failure
import com.changui.dashcoregroupchallenge.domain.ExchangeRatesFailureFactory
import javax.inject.Inject

interface ExchangeRatesRemoteDataStore {
    suspend fun fetchExchangeRates(cryptoCurrency: String): Either<Failure, ExchangeRatesApiResponse>
}

class ExchangeRatesRemoteDataStoreImpl @Inject constructor (private val apiService: BitPayApiService,
                                                            private val errorFactory: ExchangeRatesFailureFactory
)
    : ExchangeRatesRemoteDataStore {
    override suspend fun fetchExchangeRates(cryptoCurrency: String): Either<Failure, ExchangeRatesApiResponse> {
        return try {
            val apiResponse = apiService.getCryptoCurrencyExchangeRate(cryptoCurrency)
            Either.Right(apiResponse ?: ExchangeRatesApiResponse.EMPTY)
        } catch (e: Exception) {
            Either.Left(errorFactory.produce(e))
        }
    }
}

