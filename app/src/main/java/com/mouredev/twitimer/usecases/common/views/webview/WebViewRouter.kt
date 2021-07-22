package com.mouredev.twitimer.usecases.common.views.webview

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.mouredev.twitimer.usecases.base.BaseFragmentRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoRouter
import java.net.URL

/**
 * Created by MoureDev by Brais Moure on 5/29/21.
 * www.mouredev.com
 */
class WebViewRouter: BaseFragmentRouter {

    companion object {

        private const val URL = "URL"

        fun url(bundle: Bundle): String? {
            return bundle.getString(URL)
        }

    }

    override fun fragment(): WebViewFragment {
        return WebViewFragment.fragment()
    }

    fun fragment(url: String, listener: WebViewFragmentListener): WebViewFragment {
        val fragment = fragment()
        fragment.setListener(listener)
        fragment.arguments  = Bundle().apply {
            putString(URL, url)
        }
        return fragment
    }

    fun replace(manager: FragmentManager, containerId: Int, url: String, listener: WebViewFragmentListener) = manager.beginTransaction().replace(containerId, fragment(url, listener)).commit()

}

interface WebViewFragmentListener {

    fun selected(uri: Uri)

}