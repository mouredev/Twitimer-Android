package com.mouredev.twitimer.model.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by MoureDev by Brais Moure on 5/8/21.
 * www.mouredev.com
 */
data class TwitchToken(@SerializedName("access_token") var accessToken: String? = null,
                       @SerializedName("refresh_token") var refreshToken: String? = null)