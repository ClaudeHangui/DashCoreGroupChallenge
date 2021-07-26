package com.changui.dashcoregroupchallenge.view

import androidx.fragment.app.Fragment
import com.changui.dashcoregroupchallenge.domain.entity.CryptoCurrency
import com.google.android.material.snackbar.Snackbar

internal fun Fragment.showSnackBar(message: String) {
    if (view != null) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(android.R.string.ok)) { }
            .show()
    }
}


internal fun CryptoCurrency.concatToString(): String {
    return this.name + " (" + this.code + ")"
}