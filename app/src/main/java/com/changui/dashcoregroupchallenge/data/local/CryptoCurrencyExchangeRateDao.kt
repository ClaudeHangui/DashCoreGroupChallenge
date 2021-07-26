package com.changui.dashcoregroupchallenge.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CryptoCurrencyExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(item: CryptoCurrencyExchangeRate)

    @Query("delete from CryptoCurrencyExchangeRates where crypto_currency_code =:cryptoCurrencyCode")
    fun clear(cryptoCurrencyCode: String)

    @Query("select * from CryptoCurrencyExchangeRates where crypto_currency_code =:cryptoCurrencyCode")
    fun getExchangeRates(cryptoCurrencyCode: String): CryptoCurrencyExchangeRate
}