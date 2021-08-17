package com.mouredev.twitimer.util.extension

import android.os.Build
import com.mouredev.twitimer.util.Constants
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by MoureDev by Brais Moure on 5/10/21.
 * www.mouredev.com
 */

fun String.toDate(): Date? {
    var formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    var date = formatter.parse(this)
    if (date == null) {
        formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ", Constants.DEFAULT_LOCALE)
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        date = formatter.parse(this)
    }
    return date
}

fun String.toRFC3339Date(): Date? {
    val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Constants.DEFAULT_LOCALE)
    } else {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Constants.DEFAULT_LOCALE)
    }
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.parse(this)
}

fun String.uppercaseFirst(): String {
    return this.replaceFirstChar {
        it.uppercase()
    }
}

fun String.removeFirebaseInvalidCharacters(): String {
    return replace(".", "")
        .replace("#", "")
        .replace("$", "")
        .replace("[", "")
        .replace("]", "")
}

fun String.removeSocialInvalidCharacters(): String {
    return replace("@", "")
        .replace(" ", "")
}