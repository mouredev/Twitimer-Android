package com.mouredev.twitimer.usecases.menu

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.util.Constants
import com.mouredev.twitimer.util.Util

/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */

enum class Network {

    TWITCH, DISCORD, YOUTUBE, TWITTER, INSTAGRAM, GITHUB;

    val uri: String
        get() {
           return when (this) {
               TWITCH -> Constants.TWITCH_MOUREDEV_URI
               DISCORD -> Constants.DISCORD_MOUREDEV_URI
               YOUTUBE -> Constants.YOUTUBE_MOUREDEV_URI
               TWITTER -> Constants.TWITTER_MOUREDEV_URI
               INSTAGRAM -> Constants.INSTAGRAM_MOUREDEV_URI
               GITHUB -> Constants.GITHUB_MOUREDEV_URI
           }
        }

}

class MenuViewModel : ViewModel() {

    // Localization

    val updateText = R.string.countdown_reload


    val byText = R.string.menu_by
    val infoText = R.string.menu_info
    val siteText = R.string.menu_site
    val onboardingText = R.string.menu_onboarding
    fun versionText(context: Context): String {
        return context.getString(R.string.menu_version, Util.version())
    }

    // Public

    fun open(context: Context, network: Network) {
        Util.openBrowser(context, network.uri)
    }

    fun openSite(context: Context) {
        Util.openBrowser(context, Constants.TWITIMER_URI)
    }

}