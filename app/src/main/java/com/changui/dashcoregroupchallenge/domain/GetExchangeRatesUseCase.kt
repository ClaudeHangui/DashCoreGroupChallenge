package com.changui.dashcoregroupchallenge.domain

import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import javax.inject.Inject

interface GetExchangeRatesUseCase {
    suspend fun execute(params: GetExchangeRatesParams): ResultState<List<ExchangeRateModel>>
    data class GetExchangeRatesParams(var cryptoCurrencyCode: String, var cryptoCurrencyName: String )
}

class GetExchangeRatesUseCaseImpl @Inject constructor(private val repository: ExchangeRateRepository): GetExchangeRatesUseCase  {

    override suspend fun execute(params: GetExchangeRatesUseCase.GetExchangeRatesParams): ResultState<List<ExchangeRateModel>> {
        return repository.getExchangeRates(params)
    }
}