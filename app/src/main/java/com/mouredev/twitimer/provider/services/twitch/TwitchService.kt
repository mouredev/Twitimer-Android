package com.mouredev.twitimer.provider.services.twitch

import android.content.Context
import com.mouredev.twitimer.model.domain.*
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.provider.services.firebase.FirebaseRCService
import com.mouredev.twitimer.util.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by MoureDev by Brais Moure on 5/2/21.
 * www.mouredev.com
 */
object TwitchService {

    // Properties

    private const val AUTH_ERROR_STATUS_CODE = 401

    // MARK: API
    private enum class TwitchServiceAPI {

        AUTHORIZE,
        TOKEN,
        REFRESH_TOKEN,
        REVOKE,
        USER,
        SEARCH,
        SCHEDULE;

        fun baseUrl(): String {

            return when (this) {
                AUTHORIZE -> "${Constants.TWITCH_AUTH_URI}authorize?client_id=${FirebaseRCService.twitchClientID}&redirect_uri=${Constants.TWITCH_REDIRECT_URI}&response_type=code&scope=user:read:email"
                TOKEN, REFRESH_TOKEN, REVOKE -> Constants.TWITCH_AUTH_URI
                USER, SEARCH, SCHEDULE -> Constants.TWITCH_API_URI
            }
        }
    }

    // MARK: Services

    private interface TwitchAPIService {

        @POST("token?grant_type=authorization_code")
        fun token(
            @Query("client_id") clientID: String,
            @Query("client_secret") clientSecret: String,
            @Query("code") code: String,
            @Query("redirect_uri") redirectUri: String
        ): Call<TwitchToken>

        @POST("token?grant_type=refresh_token")
        fun refreshToken(
            @Query("client_id") clientID: String,
            @Query("client_secret") clientSecret: String,
            @Query("refresh_token") refreshToken: String
        ): Call<TwitchToken>

        @POST("revoke")
        fun revoke(
            @Query("client_id") clientID: String,
            @Query("token") code: String
        ): Call<Void>

        @GET("users")
        fun user(@HeaderMap headers: Map<String, String>): Call<Users>

        @GET("search/channels")
        fun search(
            @Query("query") query: String,
            @HeaderMap headers: Map<String, String>
        ): Call<UsersSearch>

        @GET("schedule")
        fun schedule(
            @Query("broadcaster_id") broadcasterId: String,
            @HeaderMap headers: Map<String, String>
        ): Call<UserSchedules>

    }

    val authorizeURL = TwitchServiceAPI.AUTHORIZE.baseUrl()

    fun token(authorizationCode: String, success: (token: TwitchToken) -> Unit, failure: () -> Unit) {

        val retrofit = Retrofit.Builder().baseUrl(TwitchServiceAPI.TOKEN.baseUrl()).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(TwitchAPIService::class.java)
        service.token(
            FirebaseRCService.twitchClientID ?: "",
            FirebaseRCService.twitchClientSecret ?: "",
            authorizationCode,
            Constants.TWITCH_REDIRECT_URI
        ).enqueue(object : Callback<TwitchToken> {

            override fun onResponse(call: Call<TwitchToken>, response: Response<TwitchToken>) {
                response.body()?.let { token ->
                    success(token)
                } ?: run {
                    failure()
                }
            }

            override fun onFailure(call: Call<TwitchToken>, t: Throwable) {
                failure()
            }

        })
    }

    fun revoke(accessToken: String, success: () -> Unit, failure: () -> Unit) {

        val retrofit = Retrofit.Builder().baseUrl(TwitchServiceAPI.REVOKE.baseUrl()).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(TwitchAPIService::class.java)
        service.revoke(FirebaseRCService.twitchClientID ?: "", accessToken).enqueue(object : Callback<Void> {

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                success()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                failure()
            }

        })
    }

    fun user(context: Context, success: (user: User) -> Unit, failure: () -> Unit, authFailure: () -> Unit, retry: Boolean = false) {

        val retrofit = Retrofit.Builder().baseUrl(TwitchServiceAPI.USER.baseUrl()).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(TwitchAPIService::class.java)
        service.user(Session.instance.authHeaders).enqueue(object : Callback<Users> {

            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                response.body()?.data?.first()?.let { user ->
                    success(user)
                } ?: run {
                    val refreshToken = Session.instance.token?.refreshToken
                    if (response.code() == AUTH_ERROR_STATUS_CODE) {
                        if (refreshToken != null && !retry) {
                            refreshToken(context, refreshToken, success = {
                                // Retry
                                user(context, success, failure, authFailure, true)
                            }, failure = {
                                failure()
                            })
                        } else {
                            // Close session
                            authFailure()
                        }
                    } else {
                        failure()
                    }
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                // Se controla en el onResponse
            }

        })
    }

    fun search(context: Context, query: String, success: (users: List<UserSearch>) -> Unit, failure: () -> Unit, retry: Boolean = false) {

        val retrofit = Retrofit.Builder().baseUrl(TwitchServiceAPI.SEARCH.baseUrl()).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(TwitchAPIService::class.java)
        service.search(query.filter { !it.isWhitespace() }, Session.instance.authHeaders).enqueue(object : Callback<UsersSearch> {

            override fun onResponse(call: Call<UsersSearch>, response: Response<UsersSearch>) {

                val users = response.body()?.data?.toMutableList()
                if (users?.isNotEmpty() == true) {
                    // Si existe un usuario concidente con la query, se sitúa de primero
                    users.indexOfFirst { user ->
                        user.broadcasterLogin?.lowercase() == query.lowercase()
                    }.let { index ->
                        if (index >= 0) {
                            val user = users[index]
                            users.removeAt(index)
                            users.add(0, user)
                        }
                    }

                    success(users)
                } else {
                    val refreshToken = Session.instance.token?.refreshToken
                    if (response.code() == AUTH_ERROR_STATUS_CODE && refreshToken != null && !retry) {
                        refreshToken(context, refreshToken, success = {
                            // Retry
                            search(context, query, success, failure, true)
                        }, failure = {
                            failure()
                        })
                    } else {
                        failure()
                    }
                }
            }

            override fun onFailure(call: Call<UsersSearch>, t: Throwable) {
                // Se controla en el onResponse
            }

        })
    }

    fun schedule(context: Context, broadcasterId: String, success: (users: List<UserScheduleSegment>) -> Unit, failure: () -> Unit, retry: Boolean = false) {

        val retrofit = Retrofit.Builder().baseUrl(TwitchServiceAPI.SCHEDULE.baseUrl()).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(TwitchAPIService::class.java)
        service.schedule(broadcasterId, Session.instance.authHeaders).enqueue(object : Callback<UserSchedules> {

            override fun onResponse(call: Call<UserSchedules>, response: Response<UserSchedules>) {
                val segments = response.body()?.data?.segments
                if (segments?.isNotEmpty() == true) {
                    success(segments)
                } else {
                    val refreshToken = Session.instance.token?.refreshToken
                    if (response.code() == AUTH_ERROR_STATUS_CODE && refreshToken != null && !retry) {
                        refreshToken(context, refreshToken, success = {
                            // Retry
                            schedule(context, broadcasterId, success, failure, true)
                        }, failure = {
                            failure()
                        })
                    } else {
                        failure()
                    }
                }
            }

            override fun onFailure(call: Call<UserSchedules>, t: Throwable) {
                // Se controla en el onResponse
            }

        })
    }

    // MARK: Private

    private fun refreshToken(context: Context, refreshToken: String, success: () -> Unit, failure: () -> Unit) {

        val retrofit = Retrofit.Builder().baseUrl(TwitchServiceAPI.REFRESH_TOKEN.baseUrl()).addConverterFactory(GsonConverterFactory.create()).build()
        val service = retrofit.create(TwitchAPIService::class.java)
        service.refreshToken(
            FirebaseRCService.twitchClientID ?: "",
            FirebaseRCService.twitchClientSecret ?: "",
            refreshToken
        ).enqueue(object : Callback<TwitchToken> {

            override fun onResponse(call: Call<TwitchToken>, response: Response<TwitchToken>) {
                response.body()?.let { token ->
                    Session.instance.save(context, token)
                    success()
                } ?: run {
                    failure()
                }
            }

            override fun onFailure(call: Call<TwitchToken>, t: Throwable) {
                failure()
            }

        })
    }

}