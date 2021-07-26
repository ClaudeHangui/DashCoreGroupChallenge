package com.changui.dashcoregroupchallenge.domain

import arrow.core.Either
import com.changui.dashcoregroupchallenge.data.error.FailureWithCache
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel


interface ExchangeRateRepository {
    suspend fun getExchangeRates(params: GetExchangeRatesUseCase.GetExchangeRatesParams):
            Either<FailureWithCache<List<ExchangeRateModel>>, List<ExchangeRateModel>>
}