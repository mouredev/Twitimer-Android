package com.mouredev.twitimer.model.session

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.mouredev.twitimer.model.domain.*
import com.mouredev.twitimer.provider.preferences.PreferencesKey
import com.mouredev.twitimer.provider.preferences.PreferencesProvider
import com.mouredev.twitimer.provider.services.firebase.FirebaseRCService
import com.mouredev.twitimer.provider.services.firebase.FirebaseRDBService
import com.mouredev.twitimer.provider.services.twitch.TwitchService
import com.mouredev.twitimer.util.Constants
import com.mouredev.twitimer.util.extension.toRFC3339Date
import com.mouredev.twitimer.util.extension.weekdayType
import java.util.*

/**
 * Created by MoureDev by Brais Moure on 5/8/21.
 * www.mouredev.com
 */

data class SortedStreaming(val streamer: User, val schedule: UserSchedule)

class Session {

    // Initialization
    companion object {
        val instance = Session()
    }

    // Properties

    var token: TwitchToken? = null
        private set
    var user: User? = null
        private set
    var streamers: MutableList<User>? = null
        private set
    private var firebaseAuthUid: String? = null

    val authHeaders: Map<String, String>
        get() {
            val clientID = FirebaseRCService.twitchClientID ?: ""
            token?.accessToken?.let { accessToken ->
                return mapOf("Client-Id" to clientID, "Authorization" to "Bearer $accessToken")
            }
            return mapOf("Client-Id" to clientID)
        }

    private enum class LanguageType(val code: String) {
        ES("ES"),
        EN("EN")
    }

    // Life cycle

    fun configure(context: Context) {

        PreferencesProvider.string(context, PreferencesKey.TOKEN)?.let {
            token = Gson().fromJson(it, TwitchToken::class.java)
        }
        PreferencesProvider.string(context, PreferencesKey.AUTH_USER)?.let {
            user = User.fromJson(it)
        }
        PreferencesProvider.string(context, PreferencesKey.STREAMERS)?.let {
            val users = Users.fromJson(it)
            streamers = users.data?.toMutableList()
        }
        firebaseAuthUid = PreferencesProvider.string(context, PreferencesKey.FIREBASE_AUTH_UID)

        defaultUser(context)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->

            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            Log.i("FCM token:", token)

            setupNotification()
        })
    }

    // Public

    fun authenticate(context: Context, authorizationCode: String, success: () -> Unit, failure: () -> Unit) {

        TwitchService.token(authorizationCode, { token ->

            save(context, token)

            TwitchService.user(context, { twitchUser ->

                val oldFollowers = HashSet(user?.followedUsers ?: arrayListOf())
                user = twitchUser

                // Intentamos recuperar sus datos si ya se encuentra dado de alta
                FirebaseRDBService.user(twitchUser, { user ->
                    mergeUsers(context, user, oldFollowers, success)
                }, {
                    FirebaseRDBService.user(twitchUser, { user ->
                        mergeUsers(context, user, oldFollowers, success)
                    }, {
                        save(context)
                        success()
                    }, true)
                })
            }, failure)
        }, failure)
    }

    fun revoke(context: Context, success: () -> Unit) {

        token?.accessToken?.let { accessToken ->
            TwitchService.revoke(accessToken, {
                clear(context)
                success()
            }, {
                clear(context)
                success()
            })
        }
    }

    fun save(context: Context, schedule: MutableList<UserSchedule>) {

        val savedSchedule = savedSchedule(context)

        if (savedSchedule == null || savedSchedule != schedule) {
            user?.schedule = schedule
            user?.let { user ->
                save(context, user)
                FirebaseRDBService.saveSchedule(user)
            }
        }
    }

    fun save(context: Context, settings: UserSettings) {

        val savedSettings = savedSettings(context)

        if (savedSettings != settings) {
            user?.settings = settings
            user?.let { user ->
                save(context, user)
                FirebaseRDBService.saveSettings(user)
            }
        }
    }

    fun saveFollow(context: Context, followedUser: User) {

        val login = followedUser.login ?: ""

        user?.followedUsers?.indexOfFirst {
            it == login
        }?.let { index ->
            if (index >= 0) {
                user?.followedUsers?.removeAt(index)
                streamers?.removeAll { user ->
                    user.login == login
                }

                setupNotification(false, login)
            } else {
                if (user?.followedUsers == null) {
                    user?.followedUsers = mutableListOf()
                }
                user?.followedUsers?.add(login)

                setupNotification(true, login)
            }
        } ?: run {
            if (user?.followedUsers == null) {
                user?.followedUsers = mutableListOf()
            }
            user?.followedUsers?.add(login)
            streamers?.add(followedUser)

            setupNotification(true, login)
        }

        user?.let { user ->
            save(context, user)
            FirebaseRDBService.saveFollowedUsers(user)
        }
    }

    fun save(context: Context, streamer: Boolean) {
        user?.streamer = streamer
        save(context)
    }

    fun fullReloadUser(context: Context, completion: () -> Unit) {

        FirebaseRCService.fetch {
            TwitchService.user(context, { twitchUser ->

                val schedule = user?.schedule
                val followedUsers = user?.followedUsers
                val streamer = user?.streamer

                user = twitchUser
                user?.schedule = schedule
                user?.followedUsers = followedUsers
                user?.streamer = streamer

                reloadUser(context, completion, true)
            }, {
                reloadUser(context, completion)
            })
        }
    }

    fun reloadUser(context: Context, completion: () -> Unit, override: Boolean = false) {

        firebaseAuth(context) {
            user?.let { currentUser ->
                FirebaseRDBService.user(currentUser, { remoteUser ->
                    saveNewUserAndReloadStreamers(context, currentUser, remoteUser, override, completion)
                }, {
                    FirebaseRDBService.user(currentUser, { remoteUser ->
                        saveNewUserAndReloadStreamers(context, currentUser, remoteUser, override, completion)
                    }, {
                        reloadStreamers(context, completion)
                    }, true)
                })
            } ?: run {
                completion()
            }
        }
    }

    fun reloadStreamers(context: Context, completion: () -> Unit) {

        val followedUsers = user?.followedUsers
        if (followedUsers != null && followedUsers.isNotEmpty()) {
            FirebaseRDBService.streamers(followedUsers) { streamers ->
                this.streamers = streamers?.toMutableList()
                val usersJSON = Users.toJson(Users(streamers))
                PreferencesProvider.set(context, PreferencesKey.STREAMERS, usersJSON)
                completion()
            }
        } else {
            this.streamers = null
            PreferencesProvider.remove(context, PreferencesKey.STREAMERS)
            completion()
        }
    }

    fun sortedStreamings(): List<SortedStreaming>? {

        streamers?.let { streamers ->

            val sortedStreamings: MutableList<SortedStreaming> = mutableListOf()

            // Se obtiene la emisión más reciente de cada streamer

            val currentDate = Date()

            streamers.forEach { streamer ->

                var nextSchedule: UserSchedule? = null

                streamer.schedule?.forEach { schedule ->

                    if (schedule.enable) {

                        val weekDate = schedule.weekDate()

                        if ((nextSchedule == null && weekDate > currentDate) || (weekDate > currentDate && weekDate < nextSchedule!!.date)) {
                            nextSchedule = schedule
                        }
                    }
                }

                nextSchedule?.let {
                    sortedStreamings.add(SortedStreaming(streamer, it))
                }
            }

            // Se ordenan los streaming por emisión

            sortedStreamings.sortBy { it.schedule.date }

            return sortedStreamings
        }
        return null
    }

    fun save(context: Context, token: TwitchToken) {
        this.token = token
        val tokenJSON = Gson().toJson(token)
        PreferencesProvider.set(context, PreferencesKey.TOKEN, tokenJSON)
    }

    fun setupNotification() {

        val topic = Constants.MAIN_NOTIFICATION_TOPIC
        val streamerTopic = Constants.STREAMER_NOTIFICATION_TOPIC
        val noStreamerTopic = Constants.NO_STREAMER_NOTIFICATION_TOPIC
        val languageCode = (Locale.getDefault().language ?: LanguageType.EN.code).uppercase()

        val subscribeLanguageType = if (languageCode == LanguageType.ES.name) LanguageType.ES else LanguageType.EN
        val unsubscribeLanguageType = if (languageCode != LanguageType.ES.name) LanguageType.ES else LanguageType.EN

        FirebaseMessaging.getInstance().apply {
            subscribeToTopic("${topic}${subscribeLanguageType.code}")
            unsubscribeFromTopic("${topic}${unsubscribeLanguageType.code}")
            if (user?.streamer == true) {
                subscribeToTopic("${streamerTopic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${streamerTopic}${unsubscribeLanguageType.code}")
                unsubscribeFromTopic("${noStreamerTopic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${noStreamerTopic}${unsubscribeLanguageType.code}")
            } else {
                subscribeToTopic("${noStreamerTopic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${noStreamerTopic}${unsubscribeLanguageType.code}")
                unsubscribeFromTopic("${streamerTopic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${streamerTopic}${unsubscribeLanguageType.code}")
            }
        }

        user?.followedUsers?.forEach { user ->
            setupNotification(true, user)
        }
    }

    fun syncSchedule(context: Context, completion: () -> Unit) {

        user?.id?.let { broadcasterId ->

            TwitchService.schedule(context, broadcasterId, { segments ->

                val defaultSchedule = defaultSchedule()

                segments.forEach { segment ->

                    val startDate = segment.startTime?.toRFC3339Date()
                    val endDate = segment.endTime?.toRFC3339Date()

                    if (segment.isRecurring == true && startDate != null && endDate != null) {

                        val weekdayType = startDate.weekdayType()

                        val diff = endDate.time - startDate.time
                        val seconds = diff / 1000
                        val minutes = seconds / 60
                        val hours = minutes / 60
                        //val days = hours / 24

                        var duration = hours
                        if (duration < 1) {
                            duration = 1
                        } else if (duration > 24) {
                            duration = 24
                        }

                        defaultSchedule.indexOfFirst { schedule ->
                            schedule.weekDay == weekdayType
                        }.let { index ->
                            if (index >= 0) {
                                defaultSchedule[index].date = startDate
                                defaultSchedule[index].duration = duration.toInt()
                                defaultSchedule[index].enable = true
                            }
                        }
                    }
                }

                PreferencesProvider.set(context, PreferencesKey.FIRST_SYNC, true)

                save(context, defaultSchedule)

                completion()
            }, {
                completion()
            })
        } ?: run {
            completion()
        }
    }

    fun savedSchedule(context: Context): List<UserSchedule>? {

        PreferencesProvider.string(context, PreferencesKey.AUTH_USER)?.let {
            return User.fromJson(it).schedule
        }
        return null
    }

    fun savedSettings(context: Context): UserSettings? {

        PreferencesProvider.string(context, PreferencesKey.AUTH_USER)?.let {
            return User.fromJson(it).settings
        }
        return null
    }

    // Private

    private fun save(context: Context, user: User) {
        PreferencesProvider.set(context, PreferencesKey.AUTH_USER, User.toJson(user))
    }

    private fun defaultUser(context: Context) {

        if (user == null) {

            // Usuario de sesión por defecto mientras no se autentica
            user = User()

            save(context, user!!)
        }
    }

    private fun saveNewUserAndReloadStreamers(context: Context, currentUser: User, newUser: User, override: Boolean, completion: () -> Unit) {
        if (override && newUser.override(currentUser)) {
            this.user = newUser
            save(context)
        } else {
            this.user = newUser
            save(context, newUser)
        }
        reloadStreamers(context, completion)
    }

    private fun mergeUsers(context: Context, user: User, oldFollowers: Set<String>, success: () -> Unit) {

        this.user?.schedule = user.schedule

        // Merge followers
        val mergedFollowers = oldFollowers.union(HashSet(user.followedUsers ?: arrayListOf())).toMutableList()
        this.user?.followedUsers = mergedFollowers
        if (mergedFollowers.isEmpty()) {
            this.user?.followedUsers = null
        }

        this.user?.streamer = user.streamer

        this.user?.followedUsers?.forEach { followedUser ->
            setupNotification(true, followedUser)
        }

        reloadStreamers(context) {
            save(context)
            success()
        }
    }

    private fun save(context: Context) {

        // TODO: Guardar en firebase solo si se han modificado datos. Ahora se guarda siempre

        if (user?.schedule?.isEmpty() != false) {
            user?.schedule = defaultSchedule()
        }

        user?.let { user ->
            save(context, user)
            FirebaseRDBService.save(user)
        }
    }

    private fun defaultSchedule(): MutableList<UserSchedule> {

        val schedule: MutableList<UserSchedule> = mutableListOf()
        val date = Calendar.getInstance()
        date[Calendar.HOUR] = 0
        date[Calendar.HOUR_OF_DAY] = 0
        date[Calendar.MINUTE] = 0
        date[Calendar.SECOND] = 0

        WeekdayType.values().forEach { weekday ->
            schedule.add(UserSchedule(false, weekday, weekday, date.time, 1, ""))
        }

        return schedule
    }

    private fun setupNotification(add: Boolean, topic: String) {

        if (topic != Constants.ADMIN_LOGIN && topic == user?.login) {
            return
        }

        val languageCode = (Locale.getDefault().language ?: LanguageType.EN.code).uppercase()
        val subscribeLanguageType = if (languageCode == LanguageType.ES.name) LanguageType.ES else LanguageType.EN
        val unsubscribeLanguageType = if (languageCode != LanguageType.ES.name) LanguageType.ES else LanguageType.EN

        FirebaseMessaging.getInstance().apply {
            if (add) {
                subscribeToTopic("${topic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${topic}${unsubscribeLanguageType.code}")
            } else {
                unsubscribeFromTopic("${topic}${subscribeLanguageType.code}")
                unsubscribeFromTopic("${topic}${unsubscribeLanguageType.code}")
            }
        }
    }

    private fun clear(context: Context) {

        user?.followedUsers?.forEach { user ->
            setupNotification(false, user)
        }

        token = null
        user = null
        streamers = null
        firebaseAuthUid = null

        PreferencesProvider.clear(context)

        defaultUser(context)

        // Firebase Auth
        FirebaseAuth.getInstance().signOut()
        firebaseAuth(context)
    }

    private fun firebaseAuth(context: Context, completion: (() -> Unit)? = null) {

        // Firebase auth anónima y permanente para poder realizar operaciones autenticadas contra Firebase
        // TODO: Intentar integrar Twitch como sistema OAuth personalizado en Firebase
        if (firebaseAuthUid == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnSuccessListener { authResult ->
                authResult.user?.uid?.let { uid ->
                    PreferencesProvider.set(context, PreferencesKey.FIREBASE_AUTH_UID, uid)
                }
                completion?.invoke()
            }.addOnFailureListener {
                completion?.invoke()
            }
        } else {
            completion?.invoke()
        }
    }

}