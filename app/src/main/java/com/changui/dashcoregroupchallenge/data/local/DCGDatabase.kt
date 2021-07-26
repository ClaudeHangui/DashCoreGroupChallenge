package com.changui.dashcoregroupchallenge.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CryptoCurrencyExchangeRate::class], version = 1)
abstract class DCGDatabase : RoomDatabase(){
    abstract fun cryptoCurrencyExchangeRateDao(): CryptoCurrencyExchangeRateDao
}