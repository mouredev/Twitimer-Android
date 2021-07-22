package com.mouredev.twitimer.util

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.DisplayMetrics
import com.mouredev.twitimer.BuildConfig
import com.mouredev.twitimer.R
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit


/**
 * Created by MoureDev by Brais Moure on 5/13/21.
 * www.mouredev.com
 */
object Util {

    fun dpToPixel(context: Context, dp: Int): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun openBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    fun version(): String {
        return "${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})"
    }

    fun countdown(context: Context, millis: Long): String? {

        if (millis < 0) {
            return null
        }

        var currentMillis = millis
        val day = TimeUnit.MILLISECONDS.toDays(currentMillis)
        currentMillis -= TimeUnit.DAYS.toMillis(day)
        val hours = TimeUnit.MILLISECONDS.toHours(currentMillis)
        currentMillis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(currentMillis)
        currentMillis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(currentMillis)

        if (day == 0L) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        return String.format(
            "%d ${if (day == 1L) context.getString(R.string.countdown_day) else context.getString(R.string.countdown_days)} %02d:%02d:%02d", day, hours, minutes, seconds
        )
    }

    fun urlEncode(url: String): String? {
        return try {
            URLEncoder.encode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            return null
        }
    }

    fun easteregg(context: Context, user: String) {
        if (user == Constants.ADMIN_LOGIN) {
            val sounds = arrayListOf(R.raw.easteregg1, R.raw.easteregg2, R.raw.easteregg3)
            val player = MediaPlayer.create(context, sounds.random())
            player.start()
        }
    }

}