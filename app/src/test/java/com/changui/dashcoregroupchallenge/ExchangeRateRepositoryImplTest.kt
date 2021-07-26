package com.changui.dashcoregroupchallenge

import com.changui.dashcoregroupchallenge.data.ExchangeRateRepositoryImpl
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRate
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateLocalDataStore
import com.changui.dashcoregroupchallenge.data.mapper.RemoteToLocalMapper
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesApiResponse
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesRemoteDataStore
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.ResultState
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import com.changui.dashcoregroupchallenge.domain.error.Failure
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ExchangeRateRepositoryImplTest {

    @MockK lateinit var remoteDataStore: ExchangeRatesRemoteDataStore
    @MockK lateinit var localDataStore: CryptoCurrencyExchangeRateLocalDataStore
    @MockK lateinit var mapper: RemoteToLocalMapper
    private lateinit var exchangeRateRepositoryImpl: ExchangeRateRepositoryImpl
    private val params = GetExchangeRatesUseCase.GetExchangeRatesParams("BTC", "Bitcoin")
    private val expectedExchangeRates = listOf(
        ExchangeRateModel("EUR", "Euros", 1.toDouble()),
        ExchangeRateModel("USD", "US Dollar", 41248.11)
    )
    private val localCryptoCurrencyExchangeRate = CryptoCurrencyExchangeRate("Bitcoin", "BTC", expectedExchangeRates)


    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        exchangeRateRepositoryImpl = ExchangeRateRepositoryImpl(remoteDataStore, mapper, localDataStore)
    }

    @Test
    fun `when fetching exchange rate for crypto currency repository return success with data`() {
        val expectedApiResponse = ExchangeRatesApiResponse(expectedExchangeRates)

        coEvery { remoteDataStore.fetchExchangeRates(params.cryptoCurrencyCode) } returns ResultState.Success(expectedApiResponse)
        coEvery { localDataStore.clearCacheForCryptoCurrency(params.cryptoCurrencyCode) } returns Unit
        coEvery { mapper.map(expectedApiResponse, params) } returns localCryptoCurrencyExchangeRate
        coEvery { localDataStore.saveExchangeRatesForCryptoCurrency(localCryptoCurrencyExchangeRate) } returns Unit
        coEvery { localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode) } returns localCryptoCurrencyExchangeRate

        val actualResponse = runBlocking { exchangeRateRepositoryImpl.getExchangeRates(params) }
        actualResponse.shouldBeInstanceOf<ResultState.Success<List<ExchangeRateModel>>>()
    }

    @Test
    fun `when fetching exchange rate for crypto currency repository return failure`() {
        val failure = Failure.NetworkError
        coEvery { remoteDataStore.fetchExchangeRates(params.cryptoCurrencyCode) } returns ResultState.Error(failure,null)
        coEvery { localDataStore.getAllExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode) } returns localCryptoCurrencyExchangeRate

        val actualResponse = runBlocking { exchangeRateRepositoryImpl.getExchangeRates(params) }
        actualResponse.shouldBeInstanceOf<ResultState.Error<List<ExchangeRateModel>>>()
    }
}