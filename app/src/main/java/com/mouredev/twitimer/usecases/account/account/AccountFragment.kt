package com.mouredev.twitimer.usecases.account.account

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.AccountFragmentBinding
import com.mouredev.twitimer.usecases.account.user.UserRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoFragmentListener
import com.mouredev.twitimer.usecases.common.views.info.InfoRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoViewType
import com.mouredev.twitimer.usecases.common.views.webview.WebViewFragmentListener
import com.mouredev.twitimer.usecases.common.views.webview.WebViewRouter

class AccountFragment : Fragment(), InfoFragmentListener, WebViewFragmentListener {

    companion object {
        fun fragment() = AccountFragment()
    }

    // Properties

    private var _binding: AccountFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AccountViewModel
    private var listener: AccountFragmentListener? = null

    // Initialization

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = AccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Model
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        // Setup
        setup()
        data()
        viewModel.load()

    }

    override fun onResume() {
        super.onResume()
        viewModel.checkSession()
    }

    // Public

    fun setListener(listener: AccountFragmentListener) {
        this.listener = listener
    }

    // Private

    private fun setup() {

    }

    private fun data() {

        activity?.let { activity ->

            viewModel.authenticated.observe(activity) { authenticated ->

                if (authenticated) {
                    UserRouter().replace(activity.supportFragmentManager, R.id.accountContainer, null)
                }
            }

            viewModel.info.observe(activity) { info ->

                if (info) {
                    InfoRouter().replace(activity.supportFragmentManager, R.id.accountContainer, InfoViewType.AUTH, listener = this)
                } else {
                    WebViewRouter().replace(activity.supportFragmentManager, R.id.accountContainer, viewModel.authorizeURL, this)
                }
            }

            viewModel.loading.observe(activity) { loading ->
                searching(loading)
            }
        }
    }

    private fun searching(show: Boolean) = if (show) {
        binding.fragmentProgressBar.visibility = View.VISIBLE
    } else {
        binding.fragmentProgressBar.visibility = View.GONE
    }

    // InfoFragmentListener

    override fun action() {
        viewModel.infoAction()
    }

    // WebViewFragmentListener

    override fun selected(uri: Uri) {

        context?.let { context ->
            viewModel.selected(context, uri, listener)
        }
    }

}