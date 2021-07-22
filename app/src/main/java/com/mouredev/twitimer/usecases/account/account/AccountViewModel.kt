package com.mouredev.twitimer.usecases.account.account

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.provider.services.twitch.TwitchService
import java.net.URL

class AccountViewModel : ViewModel() {

    // Properties

    val authorizeURL = TwitchService.authorizeURL

    // Publisehd

    val authenticated: MutableLiveData<Boolean> = MutableLiveData()
    val info: MutableLiveData<Boolean> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    // Public

    fun load() {
        loading.postValue(false)
        if (Session.instance.user?.login != null) {
            authenticated.postValue(true)
        } else {
            info.postValue(true)
        }
    }

    fun selected(context: Context, uri: Uri, listener: AccountFragmentListener?) {

        if (uri.host?.contains("localhost") == true) {
            val authorizationCode = uri.getQueryParameter("code")
            authorizationCode?.let { authCode ->
                loading.postValue(true)
                authenticate(context, authCode, listener)
            }
        }
    }

    fun infoAction() {
        info.postValue(false)
    }

    // Private

    private fun authenticate(context: Context, authorizationCode: String, listener: AccountFragmentListener?) {

        Session.instance.authenticate(context, authorizationCode, {
            load()
            listener?.authenticated()
        }, {
            load()
        })
    }

}