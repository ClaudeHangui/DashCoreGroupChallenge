package com.changui.dashcoregroupchallenge

import androidx.lifecycle.Observer
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.ResultState
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import com.changui.dashcoregroupchallenge.domain.error.Failure
import com.changui.dashcoregroupchallenge.presentation.MainViewModel
import com.changui.dashcoregroupchallenge.view.ResourcesHelper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.amshove.kluent.`should equal`
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalCoroutinesApi
@ExtendWith(InstantExecutorExtension::class)
internal class MainViewModelTest {

    @get:Rule val testCoroutineRule = TestCoroutineRule()
    private val params = GetExchangeRatesUseCase.GetExchangeRatesParams("BTC", "Bitcoin")
    @MockK lateinit var apiUsersObserver: Observer<List<ExchangeRateModel>>
    @MockK lateinit var errorObserver: Observer<MainViewModel.FailureUIState>
    @MockK lateinit var resourcesHelper: ResourcesHelper
    @MockK lateinit var getExchangeRatesUseCase: GetExchangeRatesUseCase
    private lateinit var mainViewModel: MainViewModel

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)                 // We need to define a Main Dispatcher for our viewmodel scope
        mainViewModel = MainViewModel(resourcesHelper, getExchangeRatesUseCase)
    }

    @Test
    fun `verify fetching exchange rate returns success`() {
        val expectedExchangeRateLiveDataValue = listOf(
            ExchangeRateModel("BTC", "bitcoin", 1.toDouble()),
            ExchangeRateModel("USD", "US Dollar", 41248.11)
        )

        val expectedResult = ResultState.Success(expectedExchangeRateLiveDataValue)

        testCoroutineRule.runBlockingTest {
            coEvery { getExchangeRatesUseCase.execute(params) } returns expectedResult
            mainViewModel.exchangeRatesSuccessLiveData.observeForever(apiUsersObserver)
            mainViewModel.getExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode, params.cryptoCurrencyName)
            coVerify { getExchangeRatesUseCase.execute(params) }
            val actualExchangeRateLiveDataValue = mainViewModel.exchangeRatesSuccessLiveData.value
            actualExchangeRateLiveDataValue `should equal` expectedExchangeRateLiveDataValue
        }
    }

    @Test
    fun `verify fetching exchange rate returns failure`() {
        val expectedFailure = Failure.NetworkError
        val expectedResult = ResultState.Error(expectedFailure, emptyList<ExchangeRateModel>())
        val expectedFailureUIState = MainViewModel.FailureUIState.FailureUIStateWithoutCache("Network Error")

        testCoroutineRule.runBlockingTest {
            coEvery { getExchangeRatesUseCase.execute(params) } returns expectedResult
            coEvery { resourcesHelper.networkErrorMessage } returns "Network Error"
            mainViewModel.exchangeRatesFailureLiveData.observeForever(errorObserver)
            mainViewModel.getExchangeRatesForCryptoCurrency(params.cryptoCurrencyCode, params.cryptoCurrencyName)
            coVerify { getExchangeRatesUseCase.execute(params) }
            val actualFailureUIState = mainViewModel.exchangeRatesFailureLiveData.value
            actualFailureUIState `should equal` expectedFailureUIState
        }
    }

    @AfterEach
    fun tearDown() {
        mainViewModel.exchangeRatesSuccessLiveData.removeObserver(apiUsersObserver)
        mainViewModel.exchangeRatesFailureLiveData.removeObserver(errorObserver)
        Dispatchers.resetMain()   // after usage release dispatcher
    }
}