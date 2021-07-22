package com.mouredev.twitimer.usecases.common.views.info

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import com.mouredev.twitimer.usecases.account.user.UserRouter
import com.mouredev.twitimer.usecases.base.BaseFragmentRouter


/**
 * Created by MoureDev by Brais Moure on 5/24/21.
 * www.mouredev.com
 */
class InfoRouter : BaseFragmentRouter {

    companion object {

        private const val TYPE = "TYPE"
        private const val EXTRA = "EXTRA"

        fun type(bundle: Bundle): InfoViewType? {
            bundle.getString(TYPE)?.let { type ->
                return InfoViewType.valueOf(type)
            }
            return null
        }

        fun extra(bundle: Bundle): String? {
            return bundle.getString(EXTRA)
        }

    }

    override fun fragment(): InfoFragment {
        return InfoFragment.fragment()
    }

    fun fragment(type: InfoViewType, extra: String? = null, listener: InfoFragmentListener? = null): InfoFragment {
        val fragment = fragment()
        listener?.let {
            fragment.setListener(it)
        }
        fragment.arguments  = Bundle().apply {
            putString(TYPE, type.name)
            if (type == InfoViewType.CHANNEL && extra == null) {

            }
            extra?.let {
                putString(EXTRA, extra)
            }
        }
        return fragment
    }

    fun replace(manager: FragmentManager, containerId: Int, type: InfoViewType, extra: String? = null, listener: InfoFragmentListener) = manager.beginTransaction().replace(containerId, fragment(type, extra, listener)).commit()

}

interface InfoFragmentListener {

    fun action()

}