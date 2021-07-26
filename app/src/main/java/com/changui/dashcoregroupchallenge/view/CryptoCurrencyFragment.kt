package com.changui.dashcoregroupchallenge.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.changui.dashcoregroupchallenge.R
import com.changui.dashcoregroupchallenge.databinding.CryptoCurrencyFragmentBinding
import com.changui.dashcoregroupchallenge.domain.entity.CryptoCurrency
import com.changui.dashcoregroupchallenge.presentation.CryptoCurrenciesGenerator
import com.changui.dashcoregroupchallenge.presentation.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CryptoCurrencyFragment : Fragment(R.layout.crypto_currency_fragment) {

    private var binding: CryptoCurrencyFragmentBinding ? = null
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var appContext: Context
    private var items: List<CryptoCurrency> = emptyList()
    @Inject lateinit var cryptoCurrenciesGenerator: CryptoCurrenciesGenerator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CryptoCurrencyFragmentBinding.bind(view)
        items = cryptoCurrenciesGenerator.cryptoCurrencies
        setCurrentCryptoCurrencyLabel(items[0].concatToString())
        viewModel.setAction(MainViewModel.CryptoCurrencyExchangeRateActionState.OnCryptoCurrencySelected(items.first().code, items.first().name))  // default item
        binding?.changeCryptoBtn?.setOnClickListener {
            showDialog(items.map { it.concatToString() }.toTypedArray())
        }
    }

    private fun showDialog(cryptoCurrencyList: Array<String>) {
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle("Base Currency")
        builder.setItems(cryptoCurrencyList) { _, which ->
            val selected = cryptoCurrencyList[which]
            setCurrentCryptoCurrencyLabel(selected)
            viewModel.setAction(MainViewModel.CryptoCurrencyExchangeRateActionState.OnCryptoCurrencySelected(items[which].code, items[which].name))
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setCurrentCryptoCurrencyLabel(selectedCryptoCurrency: String) {
        binding?.baseCurrencyLabel?.text = buildSpannedString {
            append("Base Currency: ")
            bold {
                append(selectedCryptoCurrency)
            }
        }
    }


    companion object {
        fun newInstance() = CryptoCurrencyFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}