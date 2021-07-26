package com.changui.dashcoregroupchallenge.data

import com.changui.dashcoregroupchallenge.domain.error.Failure
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRate
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateLocalDataStore
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesApiResponse
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesRemoteDataStore
import com.changui.dashcoregroupchallenge.domain.ExchangeRateRepository
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.ResultState
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import javax.inject.Inject

/**
 * Since the exchange rates for a crypto currency always changes, once fetched remotely, we need to clear
 * the disk for that crypto currency, insert the new values before providing it to the upper layer.
 * Before clearing the cache, we need to ensure that the server responded with some non-null data
 * In case of a failure to get new values from the server, we provide whatever data we have in our
 * local storage for that crypto currency
 */

class ExchangeRateRepositoryImpl @Inject constructor(private val remoteDataStore: ExchangeRatesRemoteDataStore,
                                                     private val localDataStore: CryptoCurrencyExchangeRateLocalDataStore)
    : ExchangeRateRepository {
    override suspend fun getExchangeRates(params: GetExchangeRatesUseCase.GetExchangeRatesParams): ResultState<List<ExchangeRateModel>> {
        return when(val remoteResult = remoteDataStore.fetchExchangeRates(params.cryptoCurrencyCode)) {
            is ResultState.Success -> {
                if (remoteResult.data == ExchangeRatesApiResponse.EMPTY){
                    val cacheExchangeRate = localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode)
                    ResultState.Success(cacheExchangeRate.toUIModel())
                } else {
                    localDataStore.clearCacheForCryptoCurrency(params.cryptoCurrencyCode)
                    localDataStore.saveExchangeRatesForCryptoCurrency(remoteResult.data.toLocalModel(params))
                    val cacheExchangeRate = localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode)
                    ResultState.Success(cacheExchangeRate.toUIModel())
                }
            }
            is ResultState.Error -> {
                val cacheExchangeRate = localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode)
                cacheExchangeRate.failureWithNullOrData(remoteResult.failure)
            }
        }
    }

    private fun CryptoCurrencyExchangeRate?.toUIModel(): List<ExchangeRateModel> {
        return this?.exchange_rates ?: emptyList()
    }

    private fun CryptoCurrencyExchangeRate?.failureWithNullOrData(failure: Failure): ResultState<List<ExchangeRateModel>> {
        return if (this == null) ResultState.Error(failure, emptyList())
        else ResultState.Error(failure, this.toUIModel())
    }

    private fun ExchangeRatesApiResponse.toLocalModel(params: GetExchangeRatesUseCase.GetExchangeRatesParams): CryptoCurrencyExchangeRate {
        return CryptoCurrencyExchangeRate(
            params.cryptoCurrencyName,
            params.cryptoCurrencyCode,
            this.data ?: emptyList()
        )
    }
}
