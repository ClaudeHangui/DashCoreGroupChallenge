package com.changui.dashcoregroupchallenge.domain

import arrow.core.Either
import com.changui.dashcoregroupchallenge.data.error.FailureWithCache
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import javax.inject.Inject

interface GetExchangeRatesUseCase : UseCaseWithParams<ExchangeRateResult, GetExchangeRatesUseCase.GetExchangeRatesParams> {
    suspend operator fun invoke(params: GetExchangeRatesParams) = execute(params)
    data class GetExchangeRatesParams(var cryptoCurrencyCode: String, var cryptoCurrencyName: String )
}

class GetExchangeRatesUseCaseImpl @Inject constructor(private val repository: ExchangeRateRepository): GetExchangeRatesUseCase  {
    override suspend fun execute(params: GetExchangeRatesUseCase.GetExchangeRatesParams): ExchangeRateResult {
        return repository.getExchangeRates(params).toResult()
    }

    private fun Either<FailureWithCache<List<ExchangeRateModel>>, List<ExchangeRateModel>>.toResult() : ExchangeRateResult =
        when(this) {
            is Either.Left -> ExchangeRateResult.ExchangeRateFailure(this.value)
            is Either.Right -> ExchangeRateResult.ExchangeRateSuccess(this.value)
        }
}