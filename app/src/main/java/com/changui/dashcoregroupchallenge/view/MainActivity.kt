package com.changui.dashcoregroupchallenge.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.changui.dashcoregroupchallenge.R
import com.changui.dashcoregroupchallenge.databinding.MainActivityBinding
import com.changui.dashcoregroupchallenge.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        this.title = "BitPay Exchange Rates"

        if (savedInstanceState == null) {
            val fm: FragmentManager = supportFragmentManager
            fm.beginTransaction()
                .replace(R.id.header_fragment, CryptoCurrencyFragment.newInstance())
                .commitNow()

            fm.beginTransaction()
                    .replace(R.id.footer_fragment, ExchangeRatesFragment.newInstance())
                    .commitNow()
        }

        viewModel.observeActionState().observe(this, { state ->
            actionObserver(state)
        })
    }

    private fun actionObserver(state: MainViewModel.CryptoCurrencyExchangeRateActionState?) {
        state ?.let {
            viewModel.actionState = it
            if (it is MainViewModel.CryptoCurrencyExchangeRateActionState.OnCryptoCurrencySelected){
                viewModel.getExchangeRatesForCryptoCurrency(it.cryptoCurrencyCode, it.cryptoCurrencyName)
            }
        }
    }
}