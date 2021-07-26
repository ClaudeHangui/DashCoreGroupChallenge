package com.changui.dashcoregroupchallenge.domain.entity

data class ExchangeRateModel(
    val code: String,
    val name: String,
    val rate: Double
) {
    companion object {
        val EMPTY = ExchangeRateModel("", "", 0.0)
    }
}
