package com.mouredev.twitimer.provider.services.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.mouredev.twitimer.model.domain.DatabaseUser
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.util.extension.removeFirebaseInvalidCharacters
import java.util.*

/**
 * Created by MoureDev by Brais Moure on 5/8/21.
 * www.mouredev.com
 */
enum class DatabaseField(val key: String) {

    // Schemes
    USERS("users"), STREAMERS("streamers"),

    // User
    ID("id"), LOGIN("login"), DISPLAY_NAME("displayName"), BROADCASTER_TYPE("broadcasterType"), DESCR("descr"),
    PROFILE_IMAGE_URL("profileImageUrl"), OFFILINE_IMAGE_URL("offlineImageUrl"),

    STREAMER("streamer"),

    // Schedule
    SCHEDULE("schedule"), ENABLE("enable"), WEEKDAY("weekDay"), DATE("date"), DURATION("duration"), TITLE("title"),

    FOLLOWED_USERS("followedUsers"),

    // Settings
    SETTINGS("settings"), ON_HOLIDAYS("onHolidays"), DISCORD("discord"), YOUTUBE("youtube"), TWITTER("twitter"), INSTAGRAM("instagram"), TIKTOK("tiktok")

}


object FirebaseRDBService {

    // Properties

    private val usersRef = FirebaseDatabase.getInstance().getReference(DatabaseField.USERS.key)
    private val streamersRef = FirebaseDatabase.getInstance().getReference(DatabaseField.STREAMERS.key)

    // MARK: Services

    fun save(user: User) {

        user.login?.let { login ->
            if (user.streamer == true) {
                streamersRef.child(login).setValue(user.toJSON())
                usersRef.child(login).removeValue()
            } else {
                usersRef.child(login).setValue(user.toJSON())
                streamersRef.child(login).removeValue()
            }
        }
    }

    fun saveSchedule(user: User) {

        user.login?.let { login ->
            streamersRef.child(login).child(DatabaseField.SCHEDULE.key).setValue(user.scheduleToJSON())
        }
    }

    fun saveSettings(user: User) {

        user.login?.let { login ->
            streamersRef.child(login).child(DatabaseField.SETTINGS.key).setValue(user.settingsToJSON())
        }
    }

    fun search(query: String, success: (users: List<User>?) -> Unit, failure: () -> Unit) {

        val validQuery = query.removeFirebaseInvalidCharacters().lowercase()
        if (validQuery.isEmpty()) {
            success(null)
            return
        }

        streamersRef.child(validQuery).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists() && snapshot.value != null) {
                snapshot.getValue<DatabaseUser>()?.let { dbUser ->
                    success(arrayListOf(dbUser.toUser()))
                }?:run {
                    success(null)
                }
            } else {
                success(null)
            }
        }.addOnFailureListener{
            failure()
        }
    }

    fun user(user: User, success: (user: User) -> Unit, failure: () -> Unit, forceStreamer: Boolean = false) {

        user.login?.let { login ->

            (if (forceStreamer || user.streamer == true) streamersRef else usersRef).child(login).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists() && snapshot.value != null) {
                    snapshot.getValue<DatabaseUser>()?.let { dbUser ->
                        success(dbUser.toUser())
                    }?:run {
                        failure()
                    }
                } else {
                    failure()
                }
            }.addOnFailureListener {
                failure()
            }
        } ?: run {
            failure()
        }
    }

    fun streamers(ids: List<String>, completion: (streamers: List<User>?) -> Unit) {

        var finishedRequests = 0
        var streamers: MutableList<User>? = null

        ids.forEach { login ->

            streamersRef.child(login).get().addOnSuccessListener { snapshot ->
                finishedRequests += 1
                if (snapshot.exists() && snapshot.value != null) {
                    snapshot.getValue<DatabaseUser>()?.let { dbUser ->
                        if (streamers == null) { streamers = arrayListOf() }
                        streamers?.add(dbUser.toUser())
                        checkStreamersSearch(ids, finishedRequests, streamers, completion)
                    }?:run {
                        checkStreamersSearch(ids, finishedRequests, streamers, completion)
                    }
                } else {
                    checkStreamersSearch(ids, finishedRequests, streamers, completion)
                }
            }.addOnFailureListener {
                finishedRequests += 1
                checkStreamersSearch(ids, finishedRequests, streamers, completion)
            }
        }
    }

    fun saveFollowedUsers(user: User) {

        user.login?.let { login ->
            (if (user.streamer == true) streamersRef else usersRef).child(login).child(DatabaseField.FOLLOWED_USERS.key).setValue(user.followedUsers)
        }
    }



    fun delete(user: User, success: () -> Unit, failure: () -> Unit) {

        user.login?.let { login ->
            (if (user.streamer == true) streamersRef else usersRef).child(login).removeValue { error, _ ->
                if (error != null) {
                    failure()
                } else {
                    success()
                }
            }
        } ?: run {
            failure()
        }
    }

    // MARK: Private

    private fun checkStreamersSearch(ids: List<String>, finishedRequests: Int, streamers: List<User>?, completion: (streamers: List<User>?) -> Unit) {

        if (finishedRequests == ids.count()) {

            // HACK: Se actualizan las fechas del calendario para que correspondan con las actuales
            streamers?.forEach { streamer ->
                streamer.updateToAvailableSchedule()
            }

            completion(streamers)
        }
    }

}