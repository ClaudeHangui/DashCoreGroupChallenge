package com.changui.dashcoregroupchallenge.domain.entity

data class CryptoCurrency(
    override val name: String,
    override val code: String
): CryptoCurrencyModelInt {
    companion object {
        val EMPTY = CryptoCurrency("", "")
    }
}

interface CryptoCurrencyModelInt {
    val name: String
    val code: String
}