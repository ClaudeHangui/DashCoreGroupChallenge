package com.changui.dashcoregroupchallenge

import arrow.core.Either
import com.changui.dashcoregroupchallenge.data.error.Failure
import com.changui.dashcoregroupchallenge.data.error.FailureWithCache
import com.changui.dashcoregroupchallenge.domain.ExchangeRateRepository
import com.changui.dashcoregroupchallenge.domain.ExchangeRateResult
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCaseImpl
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be instance of`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class GetExchangeRatesUseCaseImplTest {

    @MockK lateinit var repository: ExchangeRateRepository
    private lateinit var getExchangeRatesUseCaseImpl: GetExchangeRatesUseCaseImpl
    private val params = GetExchangeRatesUseCase.GetExchangeRatesParams("BTC")

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        getExchangeRatesUseCaseImpl = GetExchangeRatesUseCaseImpl(repository)
    }

    @Test
    fun `check if use case to fetch exchange rates returns success from the right side of the disjoint union`() {
        val exchangeRates = listOf(
            ExchangeRateModel("BTC", "bitcoin", 1.toDouble()),
            ExchangeRateModel("USD", "US Dollar", 41248.11)
        )

        coEvery { repository.getExchangeRates(params) } returns Either.Right(exchangeRates)
        val exchangeRateResult = runBlocking { getExchangeRatesUseCaseImpl.execute(params) }
        exchangeRateResult `should be instance of` ExchangeRateResult.ExchangeRateSuccess::class
    }

    @Test
    fun `check if use case to fetch exchange rates returns failure due to server error from the left side of the disjoint union`(){
        val remoteFailure = Failure.NetworkError
        coEvery { repository.getExchangeRates(params) } returns Either.Left(FailureWithCache(remoteFailure, emptyList()))
        val exchangeRateResult = runBlocking { getExchangeRatesUseCaseImpl.execute(params) }
        exchangeRateResult `should be instance of` ExchangeRateResult.ExchangeRateFailure::class
    }
}