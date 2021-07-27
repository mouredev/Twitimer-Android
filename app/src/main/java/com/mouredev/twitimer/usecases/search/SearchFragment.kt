package com.mouredev.twitimer.usecases.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.OnboardingPageFragmentBinding
import com.mouredev.twitimer.databinding.SearchFragmentBinding
import com.mouredev.twitimer.usecases.common.rows.SearchQueryRecyclerViewAdapter
import com.mouredev.twitimer.usecases.common.rows.SearchRecyclerViewAdapter
import com.mouredev.twitimer.usecases.common.views.info.InfoFragment
import com.mouredev.twitimer.usecases.common.views.info.InfoRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoViewType
import com.mouredev.twitimer.util.Constants
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.extension.enable
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.primary
import com.mouredev.twitimer.util.extension.secondary

class SearchFragment : Fragment() {

    companion object {
        fun fragment() = SearchFragment()
    }

    // Properties

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SearchViewModel
    private var isEditing = false
    private var loaded = false

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Model
        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        // Setup
        localize()
        setup()
        data()
        viewModel.load()
        loaded = true
    }

    // Public

    fun load() {
        if (loaded) {
            val query = binding.searchViewUsers.query.toString()
            context?.let { context ->
                if (query.isEmpty()) {
                    viewModel.query(context, query)
                }
            }
        }
    }

    // Private

    private fun localize() {

        binding.searchViewUsers.queryHint = getString(viewModel.searchPlaceholderText)
        binding.buttonCancel.text = getString(viewModel.cancelText)
        binding.buttonSearch.text = getString(viewModel.searchText)
    }

    private fun setup() {

        binding.frameLayoutInfoSearch.visibility = View.INVISIBLE
        binding.frameLayoutInfoChannel.visibility = View.INVISIBLE

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutInfoSearch, InfoRouter().fragment(InfoViewType.SEARCH))
        transaction?.disallowAddToBackStack()
        transaction?.commit()

        binding.searchViewUsers.font()
        binding.searchViewUsers.isIconifiedByDefault = false
        binding.searchViewUsers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(p0: String?): Boolean {
                binding.buttonSearch.enable(!(p0 == null || p0.isEmpty()))
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {

                query()
                return false
            }

        })
        binding.searchViewUsers.setOnQueryTextFocusChangeListener { _, _ ->
            binding.buttonCancel.enable(enable = true, opacity = true)
        }

        binding.searchViewUsers.setOnSearchClickListener {
            isEditing = true
            viewModel.editing()
        }

        binding.searchViewUsers.setOnCloseListener {
            cancel()
            true
        }

        context?.let { context ->

            binding.textViewSearch.font(FontSize.HEAD, color = ContextCompat.getColor(context, R.color.text))
            binding.textViewFollows.font(FontSize.SUBHEAD, color = ContextCompat.getColor(context, R.color.text))

            // Recycler view

            binding.recyclerViewQuery.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewQuery.adapter = SearchQueryRecyclerViewAdapter(context, arrayListOf()) {
                it.broadcasterLogin?.let { user ->
                    viewModel.search(context, user)
                }
            }

            binding.recyclerViewSearch.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewSearch.adapter = SearchRecyclerViewAdapter(context, arrayListOf()) {

                cancel()

                // Add listener
                showUsers()
            }
        }

        binding.buttonCancel.enable(enable = false, opacity = true)
        binding.buttonCancel.secondary {
            cancel()
        }

        binding.buttonSearch.enable(false)
        binding.buttonSearch.primary {
            query()
        }

    }

    private fun data() {

        searching(false)

        viewModel.loading.observe(viewLifecycleOwner, Observer {

            if (it) {
                searching(true)
            } else {
                searching(false)
                showUsers()
            }
        })
    }

    private fun cancel() {

        context?.let { context ->
            binding.searchViewUsers.clearFocus()
            binding.searchViewUsers.setQuery("", false)
            viewModel.cancel(context)
            isEditing = false
        }
    }

    private fun query() {

        context?.let { context ->
            binding.searchViewUsers.clearFocus()
            viewModel.query(context, binding.searchViewUsers.query.toString())
            isEditing = false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showUsers() {

        if (viewModel.users.isEmpty() && viewModel.search.isEmpty() && !isEditing) {
            if (binding.searchViewUsers.query.isEmpty()) {
                binding.recyclerViewSearch.visibility = View.INVISIBLE
                binding.recyclerViewQuery.visibility = View.INVISIBLE
                binding.textViewSearch.visibility = View.INVISIBLE
                binding.textViewFollows.visibility = View.INVISIBLE
                binding.frameLayoutInfoChannel.visibility = View.INVISIBLE
                binding.frameLayoutInfoSearch.visibility = View.VISIBLE
            } else {
                binding.recyclerViewSearch.visibility = View.INVISIBLE
                binding.recyclerViewQuery.visibility = View.INVISIBLE
                binding.textViewSearch.visibility = View.INVISIBLE
                binding.textViewFollows.visibility = View.INVISIBLE
                binding.frameLayoutInfoSearch.visibility = View.INVISIBLE
                emptyChannel(viewModel.lastSearchedUser ?: "")
                binding.frameLayoutInfoChannel.visibility = View.VISIBLE
            }
            binding.buttonCancel.enable(enable = true, opacity = true)
        } else {
            binding.frameLayoutInfoSearch.visibility = View.INVISIBLE
            binding.frameLayoutInfoChannel.visibility = View.INVISIBLE
            binding.textViewSearch.visibility = View.VISIBLE
            binding.textViewFollows.visibility = if (!isEditing) View.VISIBLE else View.INVISIBLE

            binding.textViewSearch.text = if (!isEditing) getString(viewModel.followedstreamersText) else getString(viewModel.streamersText)
            if (!isEditing) {
                binding.textViewFollows.text = "${viewModel.streamersCount}/${Constants.MAX_STREAMERS}"
            }


            if (viewModel.search.isNotEmpty()) {
                binding.buttonCancel.enable(enable = true, opacity = true)
                binding.recyclerViewQuery.visibility = View.VISIBLE
                binding.recyclerViewSearch.visibility = View.INVISIBLE

                // Recarga de query
                val adapter = binding.recyclerViewQuery.adapter as SearchQueryRecyclerViewAdapter
                adapter.users = viewModel.search
                adapter.notifyDataSetChanged()

            } else {
                if (viewModel.found) {
                    binding.buttonCancel.enable(enable = true, opacity = true)
                } else {
                    binding.buttonCancel.enable(enable = false, opacity = true)
                }
                binding.recyclerViewQuery.visibility = View.INVISIBLE
                binding.recyclerViewSearch.visibility = View.VISIBLE

                // Recarga de usuarios
                val adapter = binding.recyclerViewSearch.adapter as SearchRecyclerViewAdapter
                adapter.users = viewModel.users
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun searching(show: Boolean) = if (show) {
        binding.fragmentProgressBar.visibility = View.VISIBLE
        binding.textViewSearch.visibility = View.INVISIBLE
        binding.textViewFollows.visibility = View.INVISIBLE
        binding.frameLayoutInfoSearch.visibility = View.INVISIBLE
        binding.frameLayoutInfoChannel.visibility = View.INVISIBLE
        binding.recyclerViewSearch.visibility = View.INVISIBLE
        binding.recyclerViewQuery.visibility = View.INVISIBLE
    } else {
        binding.fragmentProgressBar.visibility = View.GONE
        binding.textViewSearch.visibility = View.VISIBLE
        binding.textViewFollows.visibility = View.VISIBLE
    }

    private fun emptyChannel(user: String) {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutInfoChannel, InfoRouter().fragment(InfoViewType.CHANNEL, user))
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

}