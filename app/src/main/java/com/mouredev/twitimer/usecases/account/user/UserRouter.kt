package com.mouredev.twitimer.usecases.account.user

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.usecases.base.BaseFragmentRouter

/**
 * Created by MoureDev by Brais Moure on 5/30/21.
 * www.mouredev.com
 */
class UserRouter: BaseFragmentRouter {

    companion object {

        private const val USER = "USER"

        fun user(bundle: Bundle): User? {
            bundle.getString(USER)?.let { user ->
                return User.fromJson(user)
            }
            return null
        }

    }

    override fun fragment(): UserFragment {
        return UserFragment.fragment()
    }

    private fun fragmentReadOnly(user: User): UserFragment {
        val fragment = fragment()
        fragment.arguments  = Bundle().apply {
            putString(USER, User.toJson(user))
        }
        return fragment
    }

    fun replace(manager: FragmentManager, containerId: Int, readOnlyUser: User?) {
        if (readOnlyUser != null) {
            manager.beginTransaction().replace(containerId, fragmentReadOnly(readOnlyUser)).commit()
        } else {
            manager.beginTransaction().replace(containerId, fragment()).commit()
        }
    }

}