package com.changui.dashcoregroupchallenge.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.changui.dashcoregroupchallenge.R
import com.changui.dashcoregroupchallenge.databinding.ExchangeRatesFragmentBinding
import com.changui.dashcoregroupchallenge.domain.entity.ExchangeRateModel
import com.changui.dashcoregroupchallenge.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeRatesFragment : Fragment(R.layout.exchange_rates_fragment) {

    companion object {
        fun newInstance() = ExchangeRatesFragment()
    }

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ExchangeRatesFragmentBinding ? = null
    private lateinit var adapter: ExchangeRateAdapter
    private val currentCryptoCurrency: String by lazy { viewModel.currentCryptoCurrency }
    private val currentCryptoCurrencyFullName: String by lazy { viewModel.currentCryptoCurrencyName }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ExchangeRatesFragmentBinding.bind(view)
        adapter = ExchangeRateAdapter(mutableListOf())
        binding?.exchangeRatesRv?.setHasFixedSize(true)
        binding?.exchangeRatesRv?.adapter = adapter

        viewModel._loadingLiveData().observe(viewLifecycleOwner, { showLoading: Boolean -> renderLoadingState(showLoading) } )
        viewModel._exchangeRatesLiveData().observe(viewLifecycleOwner, { items: List<ExchangeRateModel> -> renderListState(items) })
        viewModel._exchangeRatesErrorLiveData().observe(viewLifecycleOwner, { errorUIModel: MainViewModel.FailureUIState -> renderErrorState(errorUIModel)} )
    }

    private fun renderErrorState(failure: MainViewModel.FailureUIState) {
        when(failure) {
            is MainViewModel.FailureUIState.FailureUIStateWithCache -> {
                renderListState(failure.items)
                showSnackBar(failure.message)
            }
            is MainViewModel.FailureUIState.FailureUIStateWithoutCache -> {
                binding?.errorMessage?.text = failure.message
                binding?.retryBtn?.setOnClickListener {
                    binding?.errorGroup?.visibility = View.GONE
                    viewModel.getExchangeRatesForCryptoCurrency(currentCryptoCurrency, currentCryptoCurrencyFullName)
                }
                binding?.errorGroup?.visibility = View.VISIBLE
            }
        }
    }

    private fun renderLoadingState(showLoading: Boolean) {
        binding?.progress?.visibility = if (showLoading) {
            binding?.errorGroup?.visibility = View.GONE
            binding?.exchangeRatesRv?.visibility = View.GONE
            View.VISIBLE
        }
        else View.GONE
    }

    private fun renderListState(items: List<ExchangeRateModel>) {
        adapter.setData(items as MutableList<ExchangeRateModel>)
        binding?.exchangeRatesGroup?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}