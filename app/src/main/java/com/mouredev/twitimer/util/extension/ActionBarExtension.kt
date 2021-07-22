package com.mouredev.twitimer.util.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.mouredev.twitimer.util.UIConstants
import com.mouredev.twitimer.util.Util


/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */

fun ActionBar.titleLogo(context: Context) {

    setDisplayShowHomeEnabled(true)
    setDisplayHomeAsUpEnabled(false)
    setHomeButtonEnabled(false)

    val drawable = ContextCompat.getDrawable(context, R.drawable.twitimer_logo)
    val bitmap = (drawable as BitmapDrawable).bitmap
    val height = Util.dpToPixel(context, UIConstants.LOGO_HEIGHT).toInt()
    val width = (height * bitmap.width) / bitmap.height
    val resizedDrawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, width, height, true))

    setIcon(resizedDrawable)
    title = ""
}