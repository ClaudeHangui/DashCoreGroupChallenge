package com.changui.dashcoregroupchallenge

import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRate
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateDao
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateLocalDataStoreImpl
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CryptoCurrencyExchangeRateLocalDataStoreImplTest {

    @MockK lateinit var dao: CryptoCurrencyExchangeRateDao
    private lateinit var localDataStoreImpl: CryptoCurrencyExchangeRateLocalDataStoreImpl

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        localDataStoreImpl = CryptoCurrencyExchangeRateLocalDataStoreImpl(dao)
    }

    @Test
    fun `verify local datastore clears the cache for a give crypto currency`() {
        coEvery { dao.clear("BTC") } returns Unit
        runBlocking { localDataStoreImpl.clearCacheForCryptoCurrency("BTC") }
        coVerify(exactly = 1) { dao.clear("BTC") }
    }

    @Test
    fun `verify local datastore saves in db the list of exchange rates`() {
        val dataToInsert = CryptoCurrencyExchangeRate("Bitcoin", "BTC", emptyList())
        coEvery { dao.save(dataToInsert) } returns Unit
        runBlocking { localDataStoreImpl.saveExchangeRatesForCryptoCurrency(dataToInsert) }
        coVerify(exactly = 1) { dao.save(dataToInsert) }
    }

    @Test
    fun `verify local datastore returns a list of exchange rates for a given crypto currency`() {
        val expectedExchangeRate = CryptoCurrencyExchangeRate("Bitcoin", "BTC", listOf(ExchangeRateModel("EU", "Euros", 1.0)))
        coEvery { dao.getExchangeRates("BTC") } returns expectedExchangeRate
        val actualExchangeRate = runBlocking { localDataStoreImpl.getAllExchangeRatesForCryptoCurrency("BTC") }
        coVerify(exactly = 1) { dao.getExchangeRates("BTC") }
        actualExchangeRate.exchange_rates.size `should be equal to` 1
    }

}