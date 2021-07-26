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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val resourcesHelper: ResourcesHelper,
    private val useCase: GetExchangeRatesUseCase
) : ViewModel() {

    var currentCryptoCurrency: String = ""
    var currentCryptoCurrencyName: String = ""
    var actionState: CryptoCurrencyExchangeRateActionState? = null

    private val actionLiveData: MutableLiveData<CryptoCurrencyExchangeRateActionState> = MutableLiveData()
    fun setAction(action: CryptoCurrencyExchangeRateActionState) {
        actionLiveData.value = action
    }

    fun observeActionState(): LiveData<CryptoCurrencyExchangeRateActionState> = actionLiveData

    private val loadingMutableLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun _loadingLiveData(): LiveData<Boolean> = loadingMutableLiveData

    private val exchangeRatesMutableLiveData: MutableLiveData<List<ExchangeRateModel>> = MutableLiveData<List<ExchangeRateModel>>()
    fun _exchangeRatesLiveData(): LiveData<List<ExchangeRateModel>> = exchangeRatesMutableLiveData

    private val errorMutableLiveData: MutableLiveData<FailureUIState> = MutableLiveData<FailureUIState>()
    fun _exchangeRatesErrorLiveData(): LiveData<FailureUIState> = errorMutableLiveData


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
                    val dataResult = result.data
                    exchangeRatesMutableLiveData.value = dataResult
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