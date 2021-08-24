package com.mouredev.twitimer.usecases.account.user

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.UserFragmentBinding
import com.mouredev.twitimer.model.domain.UserSchedule
import com.mouredev.twitimer.provider.preferences.PreferencesKey
import com.mouredev.twitimer.provider.preferences.PreferencesProvider
import com.mouredev.twitimer.usecases.common.rows.ScheduleRecyclerViewAdapter
import com.mouredev.twitimer.usecases.common.views.info.InfoFragment
import com.mouredev.twitimer.usecases.common.views.info.InfoRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoViewType
import com.mouredev.twitimer.usecases.common.views.userheader.UserHeaderFragment
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.UIUtil
import com.mouredev.twitimer.util.extension.*

class UserFragment : Fragment() {

    companion object {
        fun fragment() = UserFragment()
    }

    // Properties

    private var _binding: UserFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UserViewModel
    private var infoFragment: InfoFragment? = null

    // Initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = UserFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View Model
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Arguments
        arguments?.let { arguments ->
            UserRouter.user(arguments)?.let { user ->
                viewModel.setUser(user)
            }
        }

        // Setup
        localize()
        setup()
        data()
    }

    override fun onResume() {
        super.onResume()

        // Setup
        setupHeader()
    }

    // Private

    private fun localize() {

        binding.textViewSchedule.text = getText(viewModel.scheduleText)
        binding.textViewStreamer.text = getText(viewModel.streamerText)
        binding.buttonSaveSchedule.text = getText(viewModel.saveText)
    }

    private fun setup() {

        val transaction = activity?.supportFragmentManager?.beginTransaction()

        val schedules = viewModel.getFilterSchedule()
        infoFragment = if (schedules.isNullOrEmpty()) InfoRouter().fragment(InfoViewType.SCHEDULE) else InfoRouter().fragment(InfoViewType.STREAMER)
        infoFragment?.let {
            transaction?.replace(R.id.frameLayoutInfo, it)
        }
        transaction?.disallowAddToBackStack()
        transaction?.commit()

        context?.let { context ->

            view?.setBackgroundColor(ContextCompat.getColor(context, R.color.primary))
            binding.textViewSchedule.font(FontSize.HEAD, color = ContextCompat.getColor(context, R.color.text))
            binding.textViewStreamer.font(FontSize.SUBHEAD, color = ContextCompat.getColor(context, R.color.text))

            // Buttons

            if (!viewModel.readOnly) {

                binding.imageButtonSync.setOnClickListener {
                    syncSchedule(context)
                }

                binding.switchStreamer.setOnCheckedChangeListener { _, isChecked ->
                    if (viewModel.isStreamer != isChecked) {
                        viewModel.save(context, isChecked)
                        setupBody(schedules)
                        setupHeader()
                        setupButtons()
                    }
                }

                binding.buttonSaveSchedule.enable(false)
                binding.buttonSaveSchedule.primary {
                    UIUtil.showAlert(context, getString(viewModel.saveText), getString(viewModel.saveAlertText), getString(viewModel.okText), {

                        view?.hideSoftInput()
                        val adapter = (binding.recyclerViewSchedule.adapter as ScheduleRecyclerViewAdapter)
                        viewModel.save(context, adapter.schedules)
                        binding.buttonSaveSchedule.enable(false)

                    }, getString(viewModel.cancelText))
                }
            }

            if (schedules?.isNotEmpty() == true) {
                // Recycler view

                binding.recyclerViewSchedule.layoutManager = LinearLayoutManager(context)
                binding.recyclerViewSchedule.adapter = ScheduleRecyclerViewAdapter(context, schedules, viewModel.readOnly) { schedule ->
                    checkEnableSave(context, schedule)
                }
            }

            // Sync
            if (viewModel.isStreamer && !viewModel.readOnly && !viewModel.firstSync(context)) {
                PreferencesProvider.set(context, PreferencesKey.FIRST_SYNC, true)
                syncSchedule(context)
            }
        }

        setupBody(schedules)
        setupButtons()
    }

    private fun setupHeader() {

        val transaction = activity?.supportFragmentManager?.beginTransaction()
        viewModel.getUser()?.let { user ->
            transaction?.replace(R.id.frameLayoutUserHeader, UserHeaderFragment.fragment(user, viewModel.readOnly))
        }
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

    private fun setupBody(schedule: List<UserSchedule>?) {

        binding.imageButtonSync.visibility = View.GONE

        if (viewModel.isStreamer) {
            binding.switchStreamer.isChecked = true
            if (viewModel.readOnly && schedule.isNullOrEmpty()) {
                infoFragment?.view?.visibility = View.VISIBLE
                binding.recyclerViewSchedule.visibility = View.GONE
            } else {
                infoFragment?.view?.visibility = View.GONE
                binding.recyclerViewSchedule.visibility = View.VISIBLE
            }
            if (!viewModel.readOnly) {
                binding.imageButtonSync.visibility = View.VISIBLE
            }
        } else {
            binding.switchStreamer.isChecked = false
            infoFragment?.view?.visibility = View.VISIBLE
            binding.recyclerViewSchedule.visibility = View.GONE
        }

        if (viewModel.readOnly) {
            binding.textViewStreamer.visibility = View.GONE
            binding.switchStreamer.visibility = View.GONE
        }
    }

    private fun setupButtons() {

        binding.buttonSaveSchedule.visibility = if (viewModel.isStreamer) View.VISIBLE else View.GONE

        if (viewModel.readOnly) {
            binding.layoutButtons.visibility = View.GONE
        }
    }

    private fun data() {

        // Data

    }

    private fun syncSchedule(context: Context) {

        UIUtil.showAlert(context, getString(viewModel.syncAlertTitleText), getString(viewModel.syncAlertBodyText), getString(viewModel.okText), {

            viewModel.syncSchedule(context) {

                // Recarga de calendario
                val adapter = binding.recyclerViewSchedule.adapter as ScheduleRecyclerViewAdapter
                viewModel.getFilterSchedule()?.let { schedules ->
                    adapter.schedules = schedules
                    adapter.notifyDataSetChanged()
                }
            }

        }, getString(viewModel.cancelText))
    }

    private fun checkEnableSave(context: Context, schedule: UserSchedule) {
        binding.buttonSaveSchedule.enable(viewModel.checkEnableSave(context, schedule))
    }

}