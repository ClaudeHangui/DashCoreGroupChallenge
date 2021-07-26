package com.changui.dashcoregroupchallenge.domain

import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel


interface ExchangeRateRepository {
    suspend fun getExchangeRates(params: GetExchangeRatesUseCase.GetExchangeRatesParams):
            ResultState<List<ExchangeRateModel>>
}