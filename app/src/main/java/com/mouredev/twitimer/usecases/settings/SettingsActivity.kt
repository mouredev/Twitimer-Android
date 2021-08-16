package com.mouredev.twitimer.usecases.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ActivitySettingsBinding
import com.mouredev.twitimer.usecases.common.rows.ScheduleRecyclerViewAdapter
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.UIUtil
import com.mouredev.twitimer.util.extension.*

class SettingsActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var viewModel: SettingsViewModel

    // Initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)

        // Content
        setContentView(binding.root)

        // View Model
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        // Setup
        localize()
        setup()
    }

    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_back_in_up, R.anim.slide_back_out_up)
    }

    // Private

    private fun localize() {

        binding.textViewSocialMedia.text = getText(viewModel.socialMediaText)
        binding.editTextDiscord.hint = getText(viewModel.discordPlaceholder)
        binding.editTextYouTube.hint = getText(viewModel.youtubePlaceholder)
        binding.editTextTwitter.hint = getText(viewModel.twitterPlaceholder)
        binding.editTextInstagram.hint = getText(viewModel.instagramPlaceholder)
        binding.editTextTikTok.hint = getText(viewModel.tiktokPlaceholder)
        binding.buttonCloseSession.text = getText(viewModel.closeText)
        binding.buttonSaveSettings.text = getText(viewModel.saveText)
    }

    private fun setup() {

        // UI

        // Header
        addClose()

        // Social media
        setupSocialMedia()

        // Footer

        binding.buttonCloseSession.secondary {

            UIUtil.showAlert(this, getString(viewModel.closeText), getString(viewModel.closeAlertText), getString(viewModel.okText), {
                //viewModel.close(context, listener)
            }, getString(viewModel.cancelText))
        }

        binding.buttonSaveSettings.enable(false)
        binding.buttonSaveSettings.primary {
            //viewModel.save(context, adapter.schedules)
            binding.buttonSaveSettings.enable(false)
        }

    }

    private fun setupSocialMedia() {

        binding.textViewSocialMedia.font(FontSize.HEAD, color = ContextCompat.getColor(this, R.color.text))

        // Discord

        binding.editTextDiscord.actionDone()

        binding.editTextDiscord.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //hideSoftInput()
                binding.editTextDiscord.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextDiscord.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //updated(schedule)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // YouTube

        binding.editTextYouTube.actionDone()

        binding.editTextYouTube.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //hideSoftInput()
                binding.editTextYouTube.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextYouTube.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //updated(schedule)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Twitter

        binding.editTextTwitter.actionDone()

        binding.editTextTwitter.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //hideSoftInput()
                binding.editTextTwitter.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextTwitter.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //updated(schedule)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Instagram

        binding.editTextInstagram.actionDone()

        binding.editTextInstagram.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //hideSoftInput()
                binding.editTextInstagram.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextInstagram.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //updated(schedule)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // TikTok

        binding.editTextTikTok.actionDone()

        binding.editTextTikTok.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //hideSoftInput()
                binding.editTextTikTok.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextTikTok.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //schedule.title = binding.editTextDiscord.text.toString()
                //updated(schedule)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })
    }

}