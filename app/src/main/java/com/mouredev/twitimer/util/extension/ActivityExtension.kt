package com.mouredev.twitimer.util.extension

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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