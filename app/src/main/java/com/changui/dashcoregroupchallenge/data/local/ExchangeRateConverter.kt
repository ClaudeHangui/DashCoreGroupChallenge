package com.changui.dashcoregroupchallenge.data.local

import androidx.room.TypeConverter
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ExchangeRateConverter {
    companion object {
       private val moshi =  Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        private val type = Types.newParameterizedType(List::class.java, ExchangeRateModel::class.java)
        private val adapter: JsonAdapter<List<ExchangeRateModel>> = moshi.adapter(type)

        @TypeConverter
        @JvmStatic
        fun fromJson(exchangeRates: List<ExchangeRateModel>): String = adapter.toJson(exchangeRates)

        @TypeConverter
        @JvmStatic
        fun toJson(exchangeRatesInString: String): List<ExchangeRateModel> =
            adapter.fromJson(exchangeRatesInString) ?: listOf()
    }
}