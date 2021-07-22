package com.mouredev.twitimer.util

import java.util.*

/**
 * Created by MoureDev by Brais Moure on 5/2/21.
 * www.mouredev.com
 */
object Constants {

    // Twitch
    const val TWITCH_AUTH_URI = "https://id.twitch.tv/oauth2/"
    const val TWITCH_API_URI = "https://api.twitch.tv/helix/"
    const val TWITCH_PROFILE_URI = "https://www.twitch.tv/"
    const val TWITCH_REDIRECT_URI = "http://localhost"

    // Remote
    const val REMOTE_TWITCH_CLIENT_ID = "TwitchClientID"
    const val REMOTE_TWITCH_CLIENT_SECRET = "TwitchClientSecret"

    // Generic
    const val MAX_STREAMERS = 20
    const val ADMIN_LOGIN = "mouredev"

    // Remote notifications
    const val MAIN_NOTIFICATION_TOPIC = "all"
    const val STREAMER_NOTIFICATION_TOPIC = "streamer"
    const val NO_STREAMER_NOTIFICATION_TOPIC = "nostreamer"

    // Locale
    val DEFAULT_LOCALE = Locale( "en_US_POSIX")
    const val JSON_DATE_FORMAT = "MMM d, yyyy HH:mm:ss"

    // Networks
    const val TWITIMER_URI = "https://twitimer.com"
    const val TWITCH_MOUREDEV_URI = "https://twitch.tv/mouredev"
    const val DISCORD_MOUREDEV_URI = "https://discord.gg/U3KjjfUfUJ"
    const val YOUTUBE_MOUREDEV_URI = "https://youtube.com/mouredevapps"
    const val TWITTER_MOUREDEV_URI = "https://twitter.com/mouredev"
    const val INSTAGRAM_MOUREDEV_URI = "https://instagram.com/mouredev"
    const val GITHUB_MOUREDEV_URI = "https://github.com/mouredev"

}