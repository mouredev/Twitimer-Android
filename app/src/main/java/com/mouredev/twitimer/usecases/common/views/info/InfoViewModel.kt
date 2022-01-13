package com.mouredev.twitimer.usecases.common.views.info

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.util.Constants
import com.mouredev.twitimer.util.Util
import java.util.*


enum class InfoViewType {
    COUNTDOWN, SEARCH, CHANNEL, STREAMER, AUTH, SCHEDULE, HOLIDAY, USER_HOLIDAYS
}

class InfoViewModel : ViewModel() {

    // Properties

    lateinit var type: InfoViewType
    var extra: String? = null

    // Localization

    val image: Int
        get() {
            return when (type) {
                InfoViewType.COUNTDOWN -> R.drawable.timer
                InfoViewType.SEARCH -> R.drawable.examine
                InfoViewType.CHANNEL -> R.drawable.risk_assesment
                InfoViewType.STREAMER -> R.drawable.radio_microphone
                InfoViewType.AUTH -> R.drawable.secure_connection
                InfoViewType.SCHEDULE -> R.drawable.schedule
                InfoViewType.HOLIDAY, InfoViewType.USER_HOLIDAYS -> R.drawable.vacation
            }
        }

    val title: Int
        get() {
            return when (type) {
                InfoViewType.COUNTDOWN -> R.string.info_countdown_title
                InfoViewType.SEARCH -> R.string.info_search_title
                InfoViewType.CHANNEL -> R.string.info_channel_title
                InfoViewType.STREAMER -> R.string.info_streamer_title
                InfoViewType.AUTH -> R.string.info_auth_title
                InfoViewType.SCHEDULE -> R.string.info_schedule_title
                InfoViewType.HOLIDAY, InfoViewType.USER_HOLIDAYS -> R.string.info_holiday_title
            }
        }

    val body: Int
        get() {
            return when (type) {
                InfoViewType.COUNTDOWN -> R.string.info_countdown_body
                InfoViewType.SEARCH -> R.string.info_search_body
                InfoViewType.CHANNEL -> R.string.info_channel_body
                InfoViewType.STREAMER -> R.string.info_streamer_body
                InfoViewType.AUTH -> R.string.info_auth_body
                InfoViewType.SCHEDULE -> R.string.info_schedule_body
                InfoViewType.HOLIDAY -> R.string.info_holiday_body
                InfoViewType.USER_HOLIDAYS -> R.string.info_userholidays_body
            }
        }

    fun advice(number: Int): Int {
        return when (type) {
            InfoViewType.COUNTDOWN -> R.string.info_countdown_advice_1
            InfoViewType.SEARCH -> if (number == 1) R.string.info_search_advice_1 else R.string.info_search_advice_2
            InfoViewType.CHANNEL -> R.string.info_channel_advice_1
            InfoViewType.STREAMER -> R.string.info_streamer_advice_1
            InfoViewType.AUTH -> R.string.info_auth_advice_1
            InfoViewType.SCHEDULE -> R.string.info_schedule_advice_1
            InfoViewType.HOLIDAY -> R.string.info_holiday_advice_1
            InfoViewType.USER_HOLIDAYS -> R.string.info_userholidays_advice_1
        }
    }

    fun icon(number: Int): Int? {
        return when (type) {
            InfoViewType.SEARCH -> if (number == 1) R.drawable.calendar_add else R.drawable.calendar_remove
            InfoViewType.CHANNEL, InfoViewType.USER_HOLIDAYS -> R.drawable.megaphone
            InfoViewType.STREAMER -> R.drawable.calendar
            InfoViewType.SCHEDULE -> R.drawable.time_clock_circle
            InfoViewType.HOLIDAY -> R.drawable.settings
            InfoViewType.COUNTDOWN, InfoViewType.AUTH -> null
        }
    }

    private val shareText = R.string.info_channel_share

    // Public

    fun action(listener: InfoFragmentListener?) {
        listener?.action()
    }

    fun tweet(context: Context) {

        val tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s", Util.urlEncode(context.getString(shareText, extra)))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))

        val matches: List<ResolveInfo> = context.packageManager.queryIntentActivities(intent, 0)
        for (info in matches) {
            if (info.activityInfo.packageName.lowercase(Locale.getDefault()).startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName)
            }
        }

        context.startActivity(intent)
    }

    fun share(context: Context) {
        val text = context.getString(shareText, extra)
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(share)
    }

}