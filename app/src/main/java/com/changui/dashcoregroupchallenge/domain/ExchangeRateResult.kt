package com.changui.dashcoregroupchallenge.domain

import com.changui.dashcoregroupchallenge.data.error.FailureWithCache
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel

sealed class ExchangeRateResult : ResultState {
    data class ExchangeRateSuccess(val items: List<ExchangeRateModel>): ExchangeRateResult()
    data class ExchangeRateFailure(val failureWithCache: FailureWithCache<List<ExchangeRateModel>>): ExchangeRateResult()
}
