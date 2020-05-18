package com.joao.freshgiphy.utils.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.joao.freshgiphy.R

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

// Dialog do confirm if user really wants to remove gif from favourites
fun Context.removeDialog(onConfirmClick: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(getString(R.string.dialog_remove_title))
        .setMessage(getString(R.string.dialog_remove_message))
        .setPositiveButton(getString(R.string.remove)) { _, _ -> onConfirmClick() }
        .setNegativeButton(getString(R.string.cancel)) { _, _ -> doNothing() }
        .show()
}

fun doNothing() = Unit