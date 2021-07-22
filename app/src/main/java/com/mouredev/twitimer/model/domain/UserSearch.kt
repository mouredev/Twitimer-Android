package com.mouredev.twitimer.model.domain

import com.google.gson.annotations.SerializedName

/**
 * Created by MoureDev by Brais Moure on 20/6/21.
 * www.mouredev.com
 */
data class UsersSearch(val data: List<UserSearch>? = null)

data class UserSearch(
    val id: String? = null,
    @SerializedName("broadcaster_login") val broadcasterLogin: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("thumbnail_url") val thumbnailUrl: String? = null
)