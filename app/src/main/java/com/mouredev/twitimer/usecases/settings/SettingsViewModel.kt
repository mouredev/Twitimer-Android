package com.mouredev.twitimer.usecases.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.domain.UserSettings
import com.mouredev.twitimer.model.session.Session

/**
 * Created by MoureDev by Brais Moure on 10/8/21.
 * www.mouredev.com
 */
class SettingsViewModel : ViewModel() {

    // Properties

    val settings = Session.instance.user?.settings ?: UserSettings()
    val streamer = Session.instance.user?.streamer ?: false

    // Localization

    val holidayTitleText = R.string.settings_holiday_title
    val holidayBodyText = R.string.settings_holiday_body
    val holidayAlertText = R.string.settings_holiday_alert
    val socialMediaText = R.string.settings_socialmedia
    val discordPlaceholder = R.string.settings_discord_placeholder
    val youtubePlaceholder = R.string.settings_youtube_placeholder
    val twitterPlaceholder = R.string.settings_twitter_placeholder
    val instagramPlaceholder = R.string.settings_instagram_placeholder
    val tiktokPlaceholder = R.string.settings_tiktok_placeholder
    val closeText = R.string.user_closesession
    val closeAlertText = R.string.user_closesession_alert_body
    val saveText = R.string.settings_savesettings
    val deleteTitleText = R.string.settings_deleteaccount_title
    val deleteButtonText = R.string.settings_deleteaccount_button
    val deleteAlertText = R.string.settings_deleteaccount_alert
    val okText = R.string.accept
    val cancelText = R.string.cancel

    // Public

    fun close(context: SettingsActivity) {

        Session.instance.revoke(context) {
            context.onBackPressed()
        }
    }

    fun save(context: Context) {
        Session.instance.save(context, settings)
    }

    fun enableSave(context: Context): Boolean {
        return Session.instance.savedSettings(context) != settings
    }

    fun saveHolidays(context: Context): Boolean {
        return settings.onHolidays == true && (settings.onHolidays != Session.instance.savedSettings(context)?.onHolidays)
    }

    fun restoreSaveSettings(context: Context) {
        Session.instance.user?.settings = Session.instance.savedSettings(context)
    }

    fun delete(context: SettingsActivity) {

        Session.instance.delete(context) {
            context.onBackPressed()
        }
    }

}