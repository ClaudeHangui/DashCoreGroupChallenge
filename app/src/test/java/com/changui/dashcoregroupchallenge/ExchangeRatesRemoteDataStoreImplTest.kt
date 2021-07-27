package com.changui.dashcoregroupchallenge

import com.changui.dashcoregroupchallenge.data.remote.BitPayApiService
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesApiResponse
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesFailureFactory
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesRemoteDataStoreImpl
import com.changui.dashcoregroupchallenge.domain.ResultState
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import com.changui.dashcoregroupchallenge.domain.error.Failure
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.Before
import org.junit.Test



class ExchangeRatesRemoteDataStoreImplTest {

    @MockK lateinit var apiService: BitPayApiService
    @MockK lateinit var failureFactory: ExchangeRatesFailureFactory
    private lateinit var exchangeRatesRemoteDataStoreImpl: ExchangeRatesRemoteDataStoreImpl
    private val cryptoCurrency = "BTC"

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        exchangeRatesRemoteDataStoreImpl = ExchangeRatesRemoteDataStoreImpl(apiService, failureFactory)
    }


    @Test
    fun `fetching exchange rates from server returns a Result containing the api response`() {
        val exchangeRates = listOf(
            ExchangeRateModel("BTC", "bitcoin", 1.toDouble()),
            ExchangeRateModel("USD", "US Dollar", 41248.11)
        )
        val expectedApiResponse = ExchangeRatesApiResponse(exchangeRates)
        coEvery { apiService.getCryptoCurrencyExchangeRate(cryptoCurrency) } returns expectedApiResponse

        val actualSuccessResponse = runBlocking { exchangeRatesRemoteDataStoreImpl.fetchExchangeRates(cryptoCurrency) }
        coVerify { apiService.getCryptoCurrencyExchangeRate("BTC") }
        actualSuccessResponse shouldBeInstanceOf ResultState.Success(ExchangeRatesApiResponse::class)::class
    }



    @Test(expected = Exception::class)
    fun `fetching exchange rates from server fails with an exception`() {
        coEvery { apiService.getCryptoCurrencyExchangeRate(cryptoCurrency) } throws Exception()
        val actualFailureResponse = runBlocking { exchangeRatesRemoteDataStoreImpl.fetchExchangeRates(cryptoCurrency) }
        actualFailureResponse.shouldBeInstanceOf<ResultState.Error<Failure>>()
    }

}