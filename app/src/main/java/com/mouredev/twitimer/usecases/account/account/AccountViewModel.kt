package com.mouredev.twitimer.usecases.account.account

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.provider.services.twitch.TwitchService
import com.mouredev.twitimer.util.Constants
import java.net.URL

class AccountViewModel : ViewModel() {

    // Properties

    val authorizeURL = TwitchService.authorizeURL

    private var session: String? = null

    // Publisehd

    val authenticated: MutableLiveData<Boolean> = MutableLiveData()
    val info: MutableLiveData<Boolean> = MutableLiveData()
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    // Public

    fun load() {
        loading.postValue(false)
        Session.instance.user?.login?.let { login ->
            session = login
            authenticated.postValue(true)
        } ?: run {
            info.postValue(true)
        }
    }

    fun selected(context: Context, uri: Uri, listener: AccountFragmentListener?) {

        if (uri.host?.contains(Constants.TWITCH_REDIRECT_HOST) == true) {
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

    fun checkSession() {
        val login = Session.instance.user?.login
        if (login == null && session != login) {
            session = login
            load()
        }
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