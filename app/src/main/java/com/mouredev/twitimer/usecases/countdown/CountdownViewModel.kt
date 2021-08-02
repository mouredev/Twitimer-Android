package com.mouredev.twitimer.usecases.countdown

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.session.Session

class CountdownViewModel : ViewModel() {

    // Localization

    val updateText = R.string.countdown_reload
    val upcomingText = R.string.countdown_upcoming

    // Publisehd

    val loading: MutableLiveData<Boolean> = MutableLiveData()

    var streamings = Session.instance.sortedStreamings() ?: arrayListOf()
        private set

    // Public

    fun load(context: Context) {

        Session.instance.reloadUser(context, {
            streamings = Session.instance.sortedStreamings() ?: arrayListOf()
            loading.postValue(false)
        })
    }

    fun reload(context: Context) {

        loading.postValue(true)
        Session.instance.reloadStreamers(context) {
            load(context)
        }
    }

}