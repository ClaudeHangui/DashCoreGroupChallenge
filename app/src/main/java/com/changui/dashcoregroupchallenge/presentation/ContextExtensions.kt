package com.changui.dashcoregroupchallenge.presentation

import android.util.Log
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.changui.dashcoregroupchallenge.domain.entity.CryptoCurrency
import com.google.android.material.snackbar.Snackbar

internal fun Fragment.showSnackBar(message: String) {
    if (view != null) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(android.R.string.ok)) { }
            .show()
    }
}

internal fun DialogFragment.showWithStateLoss(manager: FragmentManager, tag: String?) {
    try {
        if (manager.findFragmentByTag(tag) == null) {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        }
    } catch (e: IllegalStateException) {
        Log.e(tag,"illegal exception show: Activity has been destroyed")
    }
}


internal fun CryptoCurrency.concatToString(): String {
    return this.name + " (" + this.code + ")"
}