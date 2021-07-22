package com.mouredev.twitimer.usecases.search

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.domain.UserSearch
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.provider.services.firebase.FirebaseRDBService
import com.mouredev.twitimer.provider.services.twitch.TwitchService

class SearchViewModel : ViewModel() {

    // Properties

    var lastSearchedUser: String? = null
        private set

    // Localization

    val searchText = R.string.search
    val cancelText = R.string.cancel
    val streamersText = R.string.search_channel
    val followedstreamersText = R.string.search_followedchannels
    val searchPlaceholderText = R.string.search_bar_placeholder

    // Publisehd

    val loading: MutableLiveData<Boolean> = MutableLiveData()

    var users = Session.instance.streamers ?: arrayListOf()
        private set

    val streamersCount get() = Session.instance.user?.followedUsers?.count() ?: 0

    var search: List<UserSearch> = arrayListOf()
        private set

    // Public

    fun load() {
        loading.postValue(false)
    }

    fun query(context: Context, query: String) {

        if (query.isNotEmpty()) {

            loading.postValue(true)

            TwitchService.search(context, query, { searchUsers ->
                search = searchUsers
                load()
            }, {
                search(context, query)
            })
        } else {
            showStreamers()
        }
    }

    fun search(context: Context, user: String) {

        lastSearchedUser = user

        if (user.isNotEmpty()) {

            loading.postValue(true)

            FirebaseRDBService.search(user, { resultUsers ->
                Session.instance.reloadUser(context) {
                    this.search = arrayListOf()
                    this.users = resultUsers ?: arrayListOf()
                    load()
                }
            }, {
                Session.instance.reloadUser(context) {
                    search = arrayListOf()
                    users = arrayListOf()
                    load()
                }
            })
        } else {
            Session.instance.reloadUser(context) {
                showStreamers()
            }
        }
    }

    fun editing() {
        search = arrayListOf()
        users = arrayListOf()
        load()
    }

    fun cancel(context: Context) {
        search(context, "")
    }

    // Private

    private fun showStreamers() {
        search = arrayListOf()
        users = Session.instance.streamers ?: arrayListOf()
        load()
    }

}