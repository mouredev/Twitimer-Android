package com.mouredev.twitimer.usecases.account.user

import android.content.Context
import androidx.lifecycle.ViewModel
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.model.domain.UserSchedule
import com.mouredev.twitimer.model.domain.WeekdayType
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.provider.preferences.PreferencesKey
import com.mouredev.twitimer.provider.preferences.PreferencesProvider
import java.util.*

class UserViewModel : ViewModel() {

    // Properties

    private var user: User? = null // Read only user
    val readOnly get() = user != null
    val isStreamer get() = getUser()?.streamer ?: false

    // Localization

    val scheduleText = R.string.schedule
    val saveText = R.string.schedule_save
    val saveAlertText = R.string.user_saveschedule_alert_body
    val streamerText = R.string.user_streamer
    val syncAlertTitleText = R.string.user_syncschedule_alert_title
    val syncAlertBodyText = R.string.user_syncschedule_alert_body
    val okText = R.string.accept
    val cancelText = R.string.cancel

    // Public

    fun setUser(user: User) {
        this.user = user
    }

    fun getUser(): User? {
        return user ?: Session.instance.user
    }

    fun getFilterSchedule(): MutableList<UserSchedule>? {
        return getUser()?.schedule?.filter { schedule ->
            // Si estamos en modo lectura no se muestran los no habilitados o eventos puntuales pasados
            !readOnly || (readOnly && schedule.enable && (schedule.weekDay != WeekdayType.CUSTOM || (schedule.weekDay == WeekdayType.CUSTOM  && schedule.date > Date())))
        }?.toMutableList()
    }

    fun save(context: Context, streamer: Boolean) {
        Session.instance.save(context, streamer)
    }

    fun save(context: Context, schedule: MutableList<UserSchedule>) {
        Session.instance.save(context, schedule)
    }

    fun syncSchedule(context: Context, completion: () -> Unit) {

        Session.instance.syncSchedule(context, completion)
    }

    fun firstSync(context: Context): Boolean {
        return PreferencesProvider.bool(context, PreferencesKey.FIRST_SYNC) == true
    }

    fun checkEnableSave(context: Context, schedule: UserSchedule): Boolean {

        getUser()?.schedule?.set(schedule.weekDay.index, schedule)

        val savedSchedule = Session.instance.savedSchedule(context)
        val currentSchedule = getUser()?.schedule

        if (savedSchedule == null || savedSchedule != currentSchedule) {
            return true
        }
        return  false
    }

}

