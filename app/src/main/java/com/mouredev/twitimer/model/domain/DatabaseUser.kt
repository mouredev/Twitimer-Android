package com.mouredev.twitimer.model.domain

import com.mouredev.twitimer.util.extension.toDate
import java.util.*


/**
 * Created by MoureDev by Brais Moure on 5/8/21.
 * www.mouredev.com
 */
data class DatabaseUser(
    val id: String? = null,
    val login: String? = null,
    val displayName: String? = null,
    val broadcasterType: String? = null,
    val descr: String? = null,
    val profileImageUrl: String? = null,
    val offlineImageUrl: String? = null,
    val streamer: Int? = null,
    val schedule: List<DatabaseUserSchedule>? = null,
    val followedUsers: MutableList<String>? = null
) {

    fun toUser(): User {

        val schedule = this.schedule?.map { dbSchedule ->
            dbSchedule.toUserSchedule()
        }

        val user = User(id, login, displayName, BroadcasterType.valueFrom(broadcasterType ?: ""), descr, profileImageUrl, offlineImageUrl)
        user.streamer = streamer == 1
        user.schedule = schedule
        user.followedUsers = followedUsers ?: arrayListOf()

        return  user
    }

}

data class DatabaseUserSchedule(
    val enable: Int? = null,
    val weekDay: Int? = null,
    val date: String? = null,
    val duration: Int? = null,
    val title: String? = null
) {

    fun toUserSchedule(): UserSchedule {
        val weekDayType = WeekdayType.valueFrom(weekDay ?: 0)
        val date = date?.toDate()
        return UserSchedule(enable == 1, weekDayType, weekDayType, date ?: Date(), duration ?: 1, title ?: "")

    }

}