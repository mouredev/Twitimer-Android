package com.mouredev.twitimer.usecases.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.provider.preferences.PreferencesKey
import com.mouredev.twitimer.provider.preferences.PreferencesProvider

/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */
class HomeViewModel : ViewModel() {

    // Public

    fun defaultTab(): Int {

        val followedUsers = Session.instance.user?.followedUsers ?: arrayListOf()
        if (followedUsers.isEmpty()) {
            return R.id.home_menu_account
        } else if (Session.instance.user?.followedUsers != null && followedUsers.isNotEmpty()) {
            return R.id.home_menu_countdown
        }
        return searchTab()
    }

    fun searchTab(): Int {
        return R.id.home_menu_search
    }

    fun onboarding(context: Context): Boolean {
        return !(PreferencesProvider.bool(context, PreferencesKey.ONBOARDING) ?: false)
    }

}