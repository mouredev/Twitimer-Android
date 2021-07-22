package com.mouredev.twitimer.provider.services.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.mouredev.twitimer.util.Constants

/**
 * Created by MoureDev by Brais Moure on 5/2/21.
 * www.mouredev.com
 */
object FirebaseRCService {

    // Parameters

    var twitchClientID: String? = null
        private set

    var twitchClientSecret: String? = null
        private set

    // Public

    fun fetch(completion: () -> Unit) {

        val remoteConfig = Firebase.remoteConfig

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                twitchClientID = remoteConfig.getValue(Constants.REMOTE_TWITCH_CLIENT_ID).asString()
                twitchClientSecret = remoteConfig.getValue(Constants.REMOTE_TWITCH_CLIENT_SECRET).asString()
            }
            completion()
        }
    }

}