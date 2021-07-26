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

class ExchangeRateRepositoryImpl @Inject constructor(private val remoteDataStore: ExchangeRatesRemoteDataStore,
                                                     private val localDataStore: CryptoCurrencyExchangeRateLocalDataStore)
    : ExchangeRateRepository {
    override suspend fun getExchangeRates(params: GetExchangeRatesUseCase.GetExchangeRatesParams): ResultState<List<ExchangeRateModel>> {
        return when(val remoteResult = remoteDataStore.fetchExchangeRates(params.cryptoCurrencyCode)) {
            is ResultState.Success -> {
                localDataStore.clearCacheForCryptoCurrency(params.cryptoCurrencyCode)
                localDataStore.saveExchangeRatesForCryptoCurrency(remoteResult.data.toLocalModel(params))
                val cacheExchangeRate = localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode)
                ResultState.Success(cacheExchangeRate.toUIModel())
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
