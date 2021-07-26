package com.changui.dashcoregroupchallenge.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface BitPayApiService {
    @GET("rates/{base_crypto_currency}")
    suspend fun getCryptoCurrencyExchangeRate( @Path("base_crypto_currency") cryptoCurrency: String): ExchangeRatesApiResponse?
}