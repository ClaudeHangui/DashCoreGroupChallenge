package com.changui.dashcoregroupchallenge.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.changui.dashcoregroupchallenge.domain.error.Failure
import com.changui.dashcoregroupchallenge.domain.GetExchangeRatesUseCase
import com.changui.dashcoregroupchallenge.domain.ResultState
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import com.changui.dashcoregroupchallenge.view.ResourcesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


/**
 *
 * This viewmodel is shared between the 2 fragments and the hosting activity. This is to ensure that both fragments
 * have an up to date value of the crypto currency picked by the user. It also helps to trigger the fetching of the
 * exchange rates once a crypto currency is selected
 * In case of failure, we need to provide the error message (which is a string resource) to the ui; though we can inject
 * the application context here, we decided to give this responsibility to our ResourcesHelper class
 */

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resourcesHelper: ResourcesHelper,
    private val useCase: GetExchangeRatesUseCase
) : ViewModel() {

    var currentCryptoCurrency: String = ""
    var currentCryptoCurrencyName: String = ""
    var actionState: CryptoCurrencyExchangeRateActionState? = null

    private val actionMutableLiveData: MutableLiveData<CryptoCurrencyExchangeRateActionState> = MutableLiveData()
    fun setAction(action: CryptoCurrencyExchangeRateActionState) {
        actionMutableLiveData.value = action
    }
    val actionLiveData: LiveData<CryptoCurrencyExchangeRateActionState>
        get() = actionMutableLiveData

    private val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean>
        get() = loadingMutableLiveData

    private val exchangeRatesMutableLiveData: MutableLiveData<List<ExchangeRateModel>> = MutableLiveData<List<ExchangeRateModel>>()
    val exchangeRatesSuccessLiveData : LiveData<List<ExchangeRateModel>>
        get() = exchangeRatesMutableLiveData

    private val errorMutableLiveData: MutableLiveData<FailureUIState> = MutableLiveData<FailureUIState>()
    val exchangeRatesFailureLiveData: LiveData<FailureUIState>
        get() = errorMutableLiveData

    /**
     * We needed as much as possible to offload the work of the views so when we receive a failure result,
     * we need to determine which failure message should be provided to the UI, thus our error livedata
     * will contain the message to be displayed together with any data from the cache (if available)
     */
    fun getExchangeRatesForCryptoCurrency(cryptoCurrencyCode: String, cryptoCurrencyName: String) {
        currentCryptoCurrency = cryptoCurrencyCode
        currentCryptoCurrencyName = cryptoCurrencyName
        loadingMutableLiveData.value = true
        val params = GetExchangeRatesUseCase.GetExchangeRatesParams(cryptoCurrencyCode, cryptoCurrencyName)
        viewModelScope.launch(Dispatchers.Main) {
            when(
                val result = withContext(Dispatchers.IO) { useCase.execute(params) }
            ) {
                is ResultState.Error -> {
                    val failureDescription = when (result.failure) {
                        is Failure.NetworkError -> resourcesHelper.networkErrorMessage
                        is Failure.ServerError -> resourcesHelper.serverErrorMessage
                        is Failure.BadRequestError -> resourcesHelper.badRequestErrorMessage
                        is Failure.GatewayError -> resourcesHelper.gatewayErrorMessage
                        else -> resourcesHelper.unknownErrorMessage
                    }

                    if (result.data.isNullOrEmpty()) {
                        errorMutableLiveData.value = FailureUIState.FailureUIStateWithoutCache(failureDescription)
                    } else {
                        errorMutableLiveData.value = FailureUIState.FailureUIStateWithCache(failureDescription, result.data)
                    }

                    loadingMutableLiveData.value = false
                }
                is ResultState.Success -> {
                    exchangeRatesMutableLiveData.value = result.data
                    loadingMutableLiveData.value = false
                }
            }
        }
    }

    sealed class CryptoCurrencyExchangeRateActionState {
        data class OnCryptoCurrencySelected(val cryptoCurrencyCode: String, val cryptoCurrencyName: String): CryptoCurrencyExchangeRateActionState()
    }

    sealed class FailureUIState {
        data class FailureUIStateWithCache(val message: String, val items: List<ExchangeRateModel>): FailureUIState()
        data class FailureUIStateWithoutCache(val message: String): FailureUIState()
    }

    override fun onCleared() {
        super.onCleared()
        actionState = null
    }
}