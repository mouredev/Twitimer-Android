package com.mouredev.twitimer.usecases.account.account

import androidx.fragment.app.FragmentManager
import com.mouredev.twitimer.usecases.base.BaseFragmentRouter

/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */
class AccountRouter: BaseFragmentRouter {

    private var instance: AccountFragment? = null

    override fun fragment(): AccountFragment {
        if(instance == null) {
            instance = AccountFragment.fragment()
        }
        return instance!!
    }

    fun fragment(listener: AccountFragmentListener): AccountFragment {
        val fragment = fragment()
        fragment.setListener(listener)
        return fragment
    }

    fun add(manager: FragmentManager, containerId: Int, tag: String, listener: AccountFragmentListener) = manager.beginTransaction().add(containerId, fragment(listener), tag).commitAllowingStateLoss()

}

interface AccountFragmentListener {

    fun authenticated()

}