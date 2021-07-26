package com.changui.dashcoregroupchallenge.data.remote

import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel

data class ExchangeRatesApiResponse(
    val data: List<ExchangeRateModel>?
) {
    companion object {
        val EMPTY = ExchangeRatesApiResponse(emptyList())
    }
}