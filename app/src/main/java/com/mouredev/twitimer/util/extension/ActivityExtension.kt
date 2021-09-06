package com.mouredev.twitimer.util.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R

/**
 * Created by MoureDev by Brais Moure on 11/8/21.
 * www.mouredev.com
 */

fun AppCompatActivity.addClose() {

    supportActionBar?.title = ""
    supportActionBar?.elevation = 0f

    val closeIcon = (ContextCompat.getDrawable(this, R.drawable.close) as BitmapDrawable).bitmap
    val resizedCloseIcon: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(closeIcon, 48, 48, false))
    resizedCloseIcon.setTint(ContextCompat.getColor(this, R.color.light))
    supportActionBar?.setHomeAsUpIndicator(resizedCloseIcon)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
}

fun AppCompatActivity.hideSoftInput() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}