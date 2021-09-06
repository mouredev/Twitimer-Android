package com.mouredev.twitimer.usecases.countdown

import androidx.fragment.app.FragmentManager
import com.mouredev.twitimer.usecases.base.BaseFragmentRouter

/**
 * Created by MoureDev by Brais Moure on 5/18/21.
 * www.mouredev.com
 */
class CountdownRouter: BaseFragmentRouter {

    private var instance: CountdownFragment? = null

    override fun fragment(): CountdownFragment {
        if(instance == null) {
            instance = CountdownFragment.fragment()
        }
        return instance!!
    }

    private fun fragment(listener: CountdownFragmentListener): CountdownFragment {
        val fragment = fragment()
        fragment.setListener(listener)
        return fragment
    }

    fun add(manager: FragmentManager, containerId: Int, tag: String, listener: CountdownFragmentListener) {
        manager.beginTransaction().add(containerId, fragment(listener), tag).commitAllowingStateLoss()
    }

    override fun show(manager: FragmentManager): Int {
        val fragment = fragment()
        fragment.load()
        return manager.beginTransaction().show(fragment).commitAllowingStateLoss()
    }

}

interface CountdownFragmentListener {

    fun showSearch()

}