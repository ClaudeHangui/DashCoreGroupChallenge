package com.changui.dashcoregroupchallenge.data

import arrow.core.Either
import com.changui.dashcoregroupchallenge.data.error.Failure
import com.changui.dashcoregroupchallenge.data.error.FailureWithCache
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRate
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateLocalDataStore
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesApiResponse
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesRemoteDataStore
import com.changui.dashcoregroupchallenge.domain.ExchangeRateRepository
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(private val remoteDataStore: ExchangeRatesRemoteDataStore,
                                                     private val localDataStore: CryptoCurrencyExchangeRateLocalDataStore)
    : ExchangeRateRepository {
    override suspend fun getExchangeRates(params: GetExchangeRatesUseCase.GetExchangeRatesParams): Either<FailureWithCache<List<ExchangeRateModel>>, List<ExchangeRateModel>> {
        return remoteDataStore.fetchExchangeRates(params.cryptoCurrencyCode).fold(
            {
                val cacheExchangeRate = localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode)
                Either.Left(cacheExchangeRate.failureWithNullOrData(it))
            },
            {
                localDataStore.clearCacheForCryptoCurrency(params.cryptoCurrencyCode)
                localDataStore.saveExchangeRatesForCryptoCurrency(it.toLocalModel(params))
                val cacheExchangeRate = localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode)
                Either.Right(cacheExchangeRate.toUIModel())
            }
        )
    }


    private fun CryptoCurrencyExchangeRate?.toUIModel(): List<ExchangeRateModel> {
        return this?.exchange_rates ?: emptyList()
    }

    private fun CryptoCurrencyExchangeRate?.failureWithNullOrData(failure: Failure): FailureWithCache<List<ExchangeRateModel>> {
        return if (this == null) FailureWithCache(failure, emptyList())
        else FailureWithCache(failure, this.toUIModel())
    }

    private fun ExchangeRatesApiResponse.toLocalModel(params: GetExchangeRatesUseCase.GetExchangeRatesParams): CryptoCurrencyExchangeRate {
        return CryptoCurrencyExchangeRate(
            params.cryptoCurrencyName,
            params.cryptoCurrencyCode,
            this.data ?: emptyList()
        )
    }
}
