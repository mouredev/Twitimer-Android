package com.mouredev.twitimer.usecases.search

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mouredev.twitimer.usecases.account.account.AccountFragment
import com.mouredev.twitimer.usecases.base.BaseActivityRouter
import com.mouredev.twitimer.usecases.base.BaseFragmentRouter
import com.mouredev.twitimer.usecases.countdown.CountdownFragment

/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */
class SearchRouter: BaseFragmentRouter {

    private var instance: SearchFragment? = null

    override fun fragment(): SearchFragment {
        if(instance == null) {
            instance = SearchFragment.fragment()
        }
        return instance!!
    }

    override fun show(manager: FragmentManager): Int {
        val fragment = fragment()
        fragment.load()
        return manager.beginTransaction().show(fragment).commitAllowingStateLoss()
    }

}