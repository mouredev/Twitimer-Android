package com.mouredev.twitimer.util.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by MoureDev by Brais Moure on 20/6/21.
 * www.mouredev.com
 */

fun View.hideSoftInput() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}