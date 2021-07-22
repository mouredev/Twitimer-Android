package com.mouredev.twitimer.usecases.common.views.webview

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mouredev.twitimer.databinding.WebViewFragmentBinding
import com.mouredev.twitimer.model.session.Session


class WebViewFragment : Fragment() {

    companion object {
        fun fragment() = WebViewFragment()
    }

    // Properties

    private var _binding: WebViewFragmentBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: WebViewViewModel
    private var listener: WebViewFragmentListener? = null

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WebViewFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Model
        viewModel = ViewModelProvider(this).get(WebViewViewModel::class.java)

        // Arguments
        arguments?.let { arguments ->
            WebViewRouter.url(arguments)?.let { url ->
                viewModel.url = url
            }

            // Setup
            setup()
        }
    }

    // Public

    fun setListener(listener: WebViewFragmentListener) {
        this.listener = listener
    }

    // Private

    @SuppressLint("SetJavaScriptEnabled")
    private fun setup() {

        binding?.apply {
            webView.clearCache(true)
            webView.settings.javaScriptEnabled = true
            webView.settings.useWideViewPort = true
            webView.isHorizontalScrollBarEnabled = true
            webView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webView.webChromeClient = WebChromeClient()
            webView.webViewClient = CustomWebViewClient(listener, fragmentProgressBar)
        }

        CookieManager.getInstance().removeAllCookies {
            data()
        }
    }

    private fun data() {

        if (binding == null) {

            // FIXME: Traza temporal de error no solucionado
            FirebaseCrashlytics.getInstance().setCustomKey("WebViewFragment", Session.instance.user?.login ?: "No user login")
        }

        binding?.webView?.loadUrl(viewModel.url)
    }

    private class CustomWebViewClient(val listener: WebViewFragmentListener?, val fragmentProgressBar: FragmentContainerView) : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            url?.let {
                listener?.selected(Uri.parse(it))
                if (it.contains("code=")) {
                    view?.visibility = View.INVISIBLE
                } else {
                    view?.visibility = View.VISIBLE
                }
            }

            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            searching(true)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            searching(false)
        }

        private fun searching(show: Boolean) = if (show) {
            fragmentProgressBar.visibility = View.VISIBLE
        } else {
            fragmentProgressBar.visibility = View.GONE
        }

    }

}