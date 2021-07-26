package com.changui.dashcoregroupchallenge.data.local

import javax.inject.Inject

interface CryptoCurrencyExchangeRateLocalDataStore {
    suspend fun clearCacheForCryptoCurrency(cryptoCurrencyCode: String)
    suspend fun saveExchangeRatesForCryptoCurrency(data: CryptoCurrencyExchangeRate)
    suspend fun getAllExchangeRatesForCryptoCurrency(cryptoCurrencyCode: String): CryptoCurrencyExchangeRate
}

class CryptoCurrencyExchangeRateLocalDataStoreImpl @Inject constructor(private val dao: CryptoCurrencyExchangeRateDao):
    CryptoCurrencyExchangeRateLocalDataStore {
    override suspend fun clearCacheForCryptoCurrency(cryptoCurrencyCode: String) {
        dao.clear(cryptoCurrencyCode)
    }

    override suspend fun saveExchangeRatesForCryptoCurrency(data: CryptoCurrencyExchangeRate) {
        dao.save(data)
    }

    override suspend fun getAllExchangeRatesForCryptoCurrency(cryptoCurrencyCode: String): CryptoCurrencyExchangeRate {
        return dao.getExchangeRates(cryptoCurrencyCode)
    }


}