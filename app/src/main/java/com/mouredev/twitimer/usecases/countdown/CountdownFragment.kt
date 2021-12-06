package com.mouredev.twitimer.usecases.countdown

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.CountdownFragmentBinding
import com.mouredev.twitimer.usecases.common.rows.CountdownRecyclerViewAdapter
import com.mouredev.twitimer.usecases.common.views.info.InfoFragmentListener
import com.mouredev.twitimer.usecases.common.views.info.InfoRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoViewType
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.secondary

class CountdownFragment : Fragment(), InfoFragmentListener {

    companion object {
        fun fragment() = CountdownFragment()
    }

    // Properties

    private var _binding: CountdownFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CountdownViewModel
    private var listener: CountdownFragmentListener? = null
    private var loaded = false
    private var resumed = false

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CountdownFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Model
        viewModel = ViewModelProvider(this).get(CountdownViewModel::class.java)

        // Setup
        localize()
        setup()
        data()
        loaded = true
        load()
    }

    override fun onResume() {
        super.onResume()
        if (resumed) {
            context?.let { context ->
                viewModel.load(context)
            }
        }
        resumed = true
    }

    // Public

    fun load() {
        if (loaded) {
            context?.let { context ->
                viewModel.load(context)
            }
        }
    }

    fun setListener(listener: CountdownFragmentListener) {
        this.listener = listener
    }

    // Private

    private fun localize() {

        binding.textViewCountdown.text = getString(viewModel.upcomingText)
        binding.buttonUpdate.text = getString(viewModel.updateText)
    }

    private fun setup() {

        binding.frameLayoutInfoCountdown.visibility = View.INVISIBLE

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.frameLayoutInfoCountdown, InfoRouter().fragment(InfoViewType.COUNTDOWN, listener = this))
        transaction?.disallowAddToBackStack()
        transaction?.commit()

        context?.let { context ->

            binding.textViewCountdown.font(FontSize.HEAD, color = ContextCompat.getColor(context, R.color.text))

            // Recycler view

            binding.recyclerViewCountdown.layoutManager = LinearLayoutManager(context)
            binding.recyclerViewCountdown.adapter = CountdownRecyclerViewAdapter(context, arrayListOf())

            binding.buttonUpdate.secondary {
                viewModel.reload(context)
            }

            binding.swipeRefreshCountdown.setOnRefreshListener {
                viewModel.reload(context)
            }
        }


    }

    private fun data() {

        viewModel.loading.observe(viewLifecycleOwner, Observer {

            binding.swipeRefreshCountdown.isRefreshing = false

            if (it) {
                searching(true)
            } else {
                searching(false)
                showStreamings()
            }
        })
    }

    private fun showStreamings() {

        if (viewModel.streamings.isEmpty()) {
            binding.recyclerViewCountdown.visibility = View.INVISIBLE
            binding.textViewCountdown.visibility = View.INVISIBLE
            binding.frameLayoutInfoCountdown.visibility = View.VISIBLE
        } else {
            binding.frameLayoutInfoCountdown.visibility = View.INVISIBLE
            binding.recyclerViewCountdown.visibility = View.VISIBLE
            binding.textViewCountdown.visibility = View.VISIBLE

            // Recarga de streamings
            val adapter = binding.recyclerViewCountdown.adapter as CountdownRecyclerViewAdapter
            adapter.streamings = viewModel.streamings
            adapter.notifyDataSetChanged()
        }
    }

    private fun searching(show: Boolean) = if (show) {
        binding.fragmentProgressBar.visibility = View.VISIBLE
    } else {
        binding.fragmentProgressBar.visibility = View.GONE
    }

    // InfoFragmentListener

    override fun action() {
        listener?.showSearch()
    }

}