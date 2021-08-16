package com.mouredev.twitimer.usecases.settings

import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R

/**
 * Created by MoureDev by Brais Moure on 10/8/21.
 * www.mouredev.com
 */
class SettingsViewModel : ViewModel() {

    // Localization

    val socialMediaText = R.string.settings_socialmedia
    val discordPlaceholder = R.string.settings_discord_placeholder
    val youtubePlaceholder = R.string.settings_youtube_placeholder
    val twitterPlaceholder = R.string.settings_twitter_placeholder
    val instagramPlaceholder = R.string.settings_instagram_placeholder
    val tiktokPlaceholder = R.string.settings_tiktok_placeholder
    val closeText = R.string.user_closesession
    val closeAlertText = R.string.user_closesession_alert_body
    val saveText = R.string.settings_savesettings
    val okText = R.string.accept
    val cancelText = R.string.cancel

}