package com.mouredev.twitimer.util.extension

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.mouredev.twitimer.R
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.UIConstants

/**
 * Created by MoureDev by Brais Moure on 5/25/21.
 * www.mouredev.com
 */

fun Button.primary(listener: View.OnClickListener) {

    maxLines = 1
    ellipsize = TextUtils.TruncateAt.END
    setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.BUTTON.size.toFloat())
    setTextColor(ContextCompat.getColor(context, R.color.light))
    setTypeface(Typeface.createFromAsset(context.assets, FontType.BOLD.path), Typeface.NORMAL)
    background = ContextCompat.getDrawable(context, R.drawable.primary_button_round)
    setOnClickListener(listener)
}

/*fun Button.secondary(listener: View.OnClickListener) {

    setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.BUTTON.size.toFloat())
    setTextColor(ContextCompat.getColor(context, R.color.text))
    setTypeface(Typeface.createFromAsset(context.assets, FontType.BOLD.path), Typeface.NORMAL)
    background = ContextCompat.getDrawable(context, R.drawable.secondary_button_round)
    setOnClickListener(listener)
}*/


// Se utiliza AppCompactButton ya que hay un bug en el Button de material que no permite modificar su background desde xml
fun AppCompatButton.secondary(listener: View.OnClickListener) {

    maxLines = 1
    ellipsize = TextUtils.TruncateAt.END
    setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.BUTTON.size.toFloat())
    setTextColor(ContextCompat.getColor(context, R.color.text))
    setTypeface(Typeface.createFromAsset(context.assets, FontType.BOLD.path), Typeface.NORMAL)
    background = ContextCompat.getDrawable(context, R.drawable.secondary_button_round)
    setOnClickListener(listener)
}

fun Button.picker(color: Int = ContextCompat.getColor(context, R.color.dark)) {

    setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.SUBHEAD.size.toFloat())
    setTextColor(color)
    setTypeface(Typeface.createFromAsset(context.assets, FontType.LIGHT.path), Typeface.NORMAL)
}

fun Button.navigation(listener: View.OnClickListener) {

    setTextSize(TypedValue.COMPLEX_UNIT_SP, FontSize.BODY.size.toFloat())
    setTextColor(ContextCompat.getColor(context, R.color.text))
    setTypeface(Typeface.createFromAsset(context.assets, FontType.LIGHT.path), Typeface.NORMAL)
    setOnClickListener(listener)
}

fun Button.enable(enable: Boolean, opacity: Boolean = false) {
    if (opacity) {
        alpha = if (enable) 1f else UIConstants.SHADOW_OPACITY
    }
    isEnabled = enable
}