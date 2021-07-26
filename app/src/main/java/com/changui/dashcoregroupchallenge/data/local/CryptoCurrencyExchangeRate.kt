package com.changui.dashcoregroupchallenge.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.changui.dashcoregroupchallenge.domain.entity.CryptoCurrencyModelInt
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel

@Entity(tableName = "CryptoCurrencyExchangeRates")
@TypeConverters(ExchangeRateConverter::class)
data class CryptoCurrencyExchangeRate(
    @ColumnInfo(name = "crypto_currency_name")
    override val name: String,
    @ColumnInfo(name = "crypto_currency_code")
    override val code: String,
    val exchange_rates: List<ExchangeRateModel>,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
): CryptoCurrencyModelInt