package com.mouredev.twitimer.usecases.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.mouredev.twitimer.R
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.usecases.base.BaseActivityRouter
import com.mouredev.twitimer.usecases.common.views.userheader.UserHeaderFragment

/**
 * Created by MoureDev by Brais Moure on 14/6/21.
 * www.mouredev.com
 */
class UserRouter: BaseActivityRouter {

    companion object {

        private const val READ_ONLY_USER = "READ_ONLY_USER"

        fun user(intent: Intent) : User? {
            intent.getStringExtra(READ_ONLY_USER)?.let { userJSON ->
                return User.fromJson(userJSON)
            }
            return null
        }

    }

    override fun intent(activity: Context): Intent = Intent(activity, UserActivity::class.java)

    fun intent(activity: Context, readOnlyUser: User): Intent {
        val intent = intent(activity)
        intent.putExtra(READ_ONLY_USER, User.toJson(readOnlyUser))
        return intent
    }

    fun launch(activity: Context, readOnlyUser: User) {
        activity.startActivity(intent(activity, readOnlyUser))
        (activity as Activity).overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
    }

}