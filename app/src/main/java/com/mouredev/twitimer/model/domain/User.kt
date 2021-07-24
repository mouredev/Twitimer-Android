package com.mouredev.twitimer.model.domain

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.mouredev.twitimer.R
import com.mouredev.twitimer.provider.services.firebase.DatabaseField
import com.mouredev.twitimer.util.Constants
import com.mouredev.twitimer.util.extension.*
import java.util.*

/**
 * Created by MoureDev by Brais Moure on 5/8/21.
 * www.mouredev.com
 */
data class Users(val data: List<User>? = null) {

    // Gson

    companion object {

        fun toJson(users: Users): String {
            return GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create().toJson(users)
        }

        fun fromJson(json: String): Users {
            return GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create().fromJson(json, Users::class.java)
        }

    }

}

data class User(
    val id: String? = null,
    val login: String? = null,
    @SerializedName("display_name") var displayName: String? = null,
    @SerializedName("broadcaster_type") var broadcasterType: BroadcasterType? = null,
    @SerializedName("description") var descr: String? = null,
    @SerializedName("profile_image_url") var profileImageUrl: String? = null,
    @SerializedName("offline_image_url") var offlineImageUrl: String? = null
) {

    // Optional

    var streamer: Boolean? = null
    var schedule: List<UserSchedule>? = null
    var followedUsers: MutableList<String>? = null

    fun toJSON(): Map<String, Any> {

        val JSON: MutableMap<String, Any> = mutableMapOf(
            DatabaseField.ID.key to (id ?: ""),
            DatabaseField.LOGIN.key to (login ?: ""),
            DatabaseField.DISPLAY_NAME.key to (displayName ?: ""),
            DatabaseField.BROADCASTER_TYPE.key to (broadcasterType?.type ?: ""),
            DatabaseField.DESCR.key to (descr ?: ""),
            DatabaseField.PROFILE_IMAGE_URL.key to (profileImageUrl ?: ""),
            DatabaseField.OFFILINE_IMAGE_URL.key to (offlineImageUrl ?: ""),
            DatabaseField.STREAMER.key to (if (streamer == true) 1 else 0),
            DatabaseField.FOLLOWED_USERS.key to (followedUsers ?: arrayListOf())
        )

        JSON[DatabaseField.SCHEDULE.key] = scheduleToJSON()

        return JSON
    }

    fun scheduleToJSON(): List<Map<String, Any>> {

        val scheduleJSON: MutableList<MutableMap<String, Any>> = arrayListOf()
        schedule?.forEach { userSchedule ->
            scheduleJSON.add(userSchedule.toJSON())
        }
        return scheduleJSON
    }

    // Actualiza datos mutables del usuario. Esto ocurre cuando recuperamos de nuevo el usuario de Twitch para actualizarlo en Twitimer.
    fun override(user: User): Boolean {

        var override = false
        if (displayName != user.displayName
                || broadcasterType?.type != user.broadcasterType?.type
                || descr != user.descr
                || profileImageUrl != user.profileImageUrl
                || offlineImageUrl != user.offlineImageUrl) {
            override = true
        }

        displayName = user.displayName
        broadcasterType = user.broadcasterType
        descr = user.descr
        profileImageUrl = user.profileImageUrl
        offlineImageUrl = user.offlineImageUrl

        return override
    }

    // Actualiza el calendario del usuario a fechas disponibles a futuro
    fun updateToAvailableSchedule() {

        val calendar = Calendar.getInstance()
        val todayDate = Date()

        schedule?.forEach { daySchedule ->

            val realWeekdayType = daySchedule.date.getWeekDayType(daySchedule.weekDay.index)
            val realWeekday = realWeekdayType.toDateWeekday()

            if (daySchedule.weekDay != WeekdayType.CUSTOM && realWeekday != null) {

                val date = daySchedule.date
                calendar.time = date
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                val currentDate = todayDate.next(realWeekday, true, date, daySchedule.duration)

                calendar.time = currentDate
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                val updatedDate = calendar.time
                schedule?.get(daySchedule.weekDay.index)?.date = updatedDate
                schedule?.get(daySchedule.weekDay.index)?.currentWeekDay = realWeekdayType
            }
        }
    }

    // Gson

    companion object {

        fun toJson(user: User): String {
            return GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create().toJson(user)
        }

        fun fromJson(json: String): User {
            return GsonBuilder().setDateFormat(Constants.JSON_DATE_FORMAT).create().fromJson(json, User::class.java)
        }

    }

}

data class UserSchedule(
    var enable: Boolean = false,
    val weekDay: WeekdayType,
    var currentWeekDay: WeekdayType,
    var date: Date,
    var duration: Int,
    var title: String
) {

    fun toJSON(): MutableMap<String, Any> {

        // HACK: Al guardar un horario establecemos la fecha local del usuario para ese día de la semana. Esto nos servirá para calcular con qué día de la semana se corresponde en caso de cambio horario.
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        weekDay.toDateWeekday()?.let { nextWeekday ->

            val components = Calendar.getInstance()
            components.time = date
            components.timeZone = TimeZone.getDefault()
            components.set(Calendar.DAY_OF_MONTH, day)
            components.set(Calendar.MONTH, month)
            components.set(Calendar.YEAR, year)

            val updatedDate = components.time
            date = updatedDate

            date = date.next(nextWeekday, true)
        }

        return mutableMapOf(
            DatabaseField.ENABLE.key to (if (enable) 1 else 0),
            DatabaseField.WEEKDAY.key to weekDay.index,
            DatabaseField.DATE.key to date.toJSON(),
            DatabaseField.DURATION.key to duration,
            DatabaseField.TITLE.key to title
        )
    }

    // Obtiene la fecha ficticia más próxima al día de la semana
    fun weekDate(): Date {

        weekDay.toDateWeekday()?.let { nextWeekday ->
            val nextDate = date.next(nextWeekday, true, date, duration)
            var finishDate = nextDate
            finishDate = Date(finishDate.time + (1000 * 60 * 60 * duration))
            if (Date() < finishDate) {
                return finishDate
            }
            return date.next(nextWeekday, false)
        }
        return date
    }

    fun formattedDate(): String {
        return date.longFormat()
    }

}

enum class BroadcasterType(val type: String) {

    @SerializedName("partner")
    PARTNER("partner"),

    @SerializedName("affiliate")
    AFFILIATE("affiliate"),

    @SerializedName("")
    NONE("");

    companion object {

        fun valueFrom(type: String): BroadcasterType {
            return BroadcasterType.values().find { it.type == type } ?: BroadcasterType.NONE
        }
    }

}

enum class WeekdayType(val index: Int) {

    CUSTOM(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    val nameKey: Int
        get() {
            return when (this) {
                CUSTOM -> R.string.schedule_0
                MONDAY -> R.string.schedule_1
                TUESDAY -> R.string.schedule_2
                WEDNESDAY -> R.string.schedule_3
                THURSDAY -> R.string.schedule_4
                FRIDAY -> R.string.schedule_5
                SATURDAY -> R.string.schedule_6
                SUNDAY -> R.string.schedule_7
            }
        }

    fun toDateWeekday(): Weekday? {
        return when (this) {
            CUSTOM -> null
            MONDAY -> Weekday.MONDAY
            TUESDAY -> Weekday.TUESDAY
            WEDNESDAY -> Weekday.WEDNESDAY
            THURSDAY -> Weekday.THURSDAY
            FRIDAY -> Weekday.FRIDAY
            SATURDAY -> Weekday.SATURDAY
            SUNDAY -> Weekday.SUNDAY
        }
    }

    companion object {

        fun valueFrom(index: Int): WeekdayType {
            return values().find { it.index == index } ?: CUSTOM
        }
    }

}