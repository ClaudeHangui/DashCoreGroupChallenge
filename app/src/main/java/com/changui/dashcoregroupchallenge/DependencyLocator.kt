package com.changui.dashcoregroupchallenge

import android.content.Context
import androidx.room.Room
import com.changui.dashcoregroupchallenge.data.ExchangeRateRepositoryImpl
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateDao
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateLocalDataStore
import com.changui.dashcoregroupchallenge.data.local.CryptoCurrencyExchangeRateLocalDataStoreImpl
import com.changui.dashcoregroupchallenge.data.local.DCGDatabase
import com.changui.dashcoregroupchallenge.data.mapper.RemoteToLocalMapper
import com.changui.dashcoregroupchallenge.data.remote.BitPayApiService
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesRemoteDataStore
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesRemoteDataStoreImpl
import com.changui.dashcoregroupchallenge.domain.ExchangeRateRepository
import com.changui.dashcoregroupchallenge.data.remote.ExchangeRatesFailureFactory
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCaseImpl
import com.changui.dashcoregroupchallenge.domain.scope.CoroutineDispatchers
import com.changui.dashcoregroupchallenge.domain.scope.CoroutineDispatchersImpl
import com.changui.dashcoregroupchallenge.presentation.CryptoCurrenciesGenerator
import com.changui.dashcoregroupchallenge.view.ResourcesHelper
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


const val BASE_URL = "https://bitpay.com/"
const val TIMEOUT = 60L
const val DB_NAME = "dash_core_group.db"

@InstallIn(SingletonComponent::class)
@Module(includes = [AbstractBindingProvision::class])
object NonStaticProvision {

    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): DCGDatabase {
        return Room.databaseBuilder(
            appContext,
            DCGDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDao(database: DCGDatabase): CryptoCurrencyExchangeRateDao = database.cryptoCurrencyExchangeRateDao()

    @Provides
    fun provideResourceHelper(@ApplicationContext appContext: Context): ResourcesHelper = ResourcesHelper(appContext)

    @Provides
    fun provideResourceGenerator(resourcesHelper: ResourcesHelper): CryptoCurrenciesGenerator = CryptoCurrenciesGenerator(resourcesHelper)

    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    fun provideApiInterceptor(): Interceptor = Interceptor { chain: Interceptor.Chain ->
        var request = chain.request()
        val url = request.url.newBuilder().build()
        request = request.newBuilder().url(url).build()
        chain.proceed(request)
    }

    @Provides
    fun provideOkHttpClient(
        apiInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .addInterceptor(apiInterceptor)
        .addInterceptor(loggingInterceptor)
        .retryOnConnectionFailure(true)
        .build()

    @Provides
    fun provideMoshi():Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Provides
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    fun provideApi(retrofit: Retrofit): BitPayApiService {
        return retrofit.create(BitPayApiService::class.java)
    }

    @Provides
    fun provideErrorFactory(): ExchangeRatesFailureFactory = ExchangeRatesFailureFactory()

    @Provides
    fun provideMapper(): RemoteToLocalMapper = RemoteToLocalMapper()

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatchers {
        return CoroutineDispatchersImpl()
    }
}

@InstallIn(SingletonComponent::class)
@Module
abstract class AbstractBindingProvision {
    @Binds
    abstract fun bindRepository(exchangeRateRepository: ExchangeRateRepositoryImpl): ExchangeRateRepository

    @Binds
    abstract fun bindUseCase(getExchangeRatesUseCase: GetExchangeRatesUseCaseImpl): GetExchangeRatesUseCase

    @Binds
    abstract fun bindRemoteDataStore(exchangeRatesRemoteDataStoreImpl: ExchangeRatesRemoteDataStoreImpl): ExchangeRatesRemoteDataStore

    @Binds
    abstract fun bindLocalDataStore(localDataStoreImpl: CryptoCurrencyExchangeRateLocalDataStoreImpl): CryptoCurrencyExchangeRateLocalDataStore
}