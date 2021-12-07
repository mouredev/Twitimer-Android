package com.mouredev.twitimer.usecases.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ActivitySettingsBinding
import com.mouredev.twitimer.usecases.common.views.info.InfoRouter
import com.mouredev.twitimer.usecases.common.views.info.InfoViewType
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.UIUtil
import com.mouredev.twitimer.util.extension.*

class SettingsActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var viewModel: SettingsViewModel

    private var currentEditText: EditText? = null

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
        load()
    }

    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed() 
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.restoreSaveSettings(this)
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
        binding.buttonDeleteAccount.text = getText(viewModel.deleteButtonText)
    }

    private fun setup() {

        // UI

        // Header
        addClose()

        // Social media
        setupSocialMedia()

        // Footer
        setupFooter()

    }

    private fun setupSocialMedia() {

        // Social media

        binding.textViewSocialMedia.font(FontSize.HEAD, color = ContextCompat.getColor(this, R.color.text))

        // Discord

        binding.editTextDiscord.font(FontSize.BODY, color = ContextCompat.getColor(this, R.color.text))
        binding.editTextDiscord.actionDone()

        binding.editTextDiscord.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.settings.discord = binding.editTextDiscord.text.toString()
                hideSoftInput()
                textView.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextDiscord.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                currentEditText = binding.editTextDiscord
                viewModel.settings.discord = binding.editTextDiscord.text.toString()
                checkEnableSave()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // YouTube

        binding.editTextYouTube.font(FontSize.BODY, color = ContextCompat.getColor(this, R.color.text))
        binding.editTextYouTube.actionDone()

        binding.editTextYouTube.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.settings.youtube = binding.editTextYouTube.text.toString()
                hideSoftInput()
                textView.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextYouTube.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                currentEditText = binding.editTextYouTube
                viewModel.settings.youtube = binding.editTextYouTube.text.toString()
                checkEnableSave()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Twitter

        binding.editTextTwitter.font(FontSize.BODY, color = ContextCompat.getColor(this, R.color.text))
        binding.editTextTwitter.actionDone()

        binding.editTextTwitter.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.settings.twitter = binding.editTextTwitter.text.toString()
                hideSoftInput()
                textView.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextTwitter.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                currentEditText = binding.editTextTwitter
                viewModel.settings.twitter = binding.editTextTwitter.text.toString()
                checkEnableSave()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Instagram

        binding.editTextInstagram.font(FontSize.BODY, color = ContextCompat.getColor(this, R.color.text))
        binding.editTextInstagram.actionDone()

        binding.editTextInstagram.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.settings.instagram = binding.editTextInstagram.text.toString()
                hideSoftInput()
                textView.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextInstagram.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                currentEditText = binding.editTextInstagram
                viewModel.settings.instagram = binding.editTextInstagram.text.toString()
                checkEnableSave()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // TikTok

        binding.editTextTikTok.font(FontSize.BODY, color = ContextCompat.getColor(this, R.color.text))
        binding.editTextTikTok.actionDone()

        binding.editTextTikTok.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.settings.tiktok = binding.editTextTikTok.text.toString()
                hideSoftInput()
                textView.clearFocus()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.editTextTikTok.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                currentEditText = binding.editTextTikTok
                viewModel.settings.tiktok = binding.editTextTikTok.text.toString()
                checkEnableSave()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }
        })

        // Delete account

        binding.textViewDeleteAccount.font(FontSize.HEAD, color = ContextCompat.getColor(this, R.color.text))

        binding.buttonDeleteAccount.destroy {
            UIUtil.showAlert(this, getString(viewModel.deleteButtonText), getString(viewModel.deleteAlertText), getString(viewModel.deleteTitleText), {
                hideSoftInput()
                viewModel.delete(this)
                currentEditText?.clearFocus()
            }, getString(viewModel.cancelText), true)
        }
    }

    private fun setupFooter() {

        binding.buttonCloseSession.secondary {

            UIUtil.showAlert(this, getString(viewModel.closeText), getString(viewModel.closeAlertText), getString(viewModel.okText), {
                hideSoftInput()
                viewModel.close(this)
                currentEditText?.clearFocus()
            }, getString(viewModel.cancelText))
        }

        binding.buttonSaveSettings.enable(false)
        binding.buttonSaveSettings.primary {
            hideSoftInput()
            viewModel.save(this)
            binding.buttonSaveSettings.enable(false)
            currentEditText?.clearFocus()
        }
    }

    private fun load() {

        if (viewModel.streamer) {

            binding.frameLayoutInfo.visibility = View.GONE

            binding.editTextDiscord.setText(viewModel.settings.discord)
            binding.editTextYouTube.setText(viewModel.settings.youtube)
            binding.editTextTwitter.setText(viewModel.settings.twitter)
            binding.editTextInstagram.setText(viewModel.settings.instagram)
            binding.editTextTikTok.setText(viewModel.settings.tiktok)

        } else {

            binding.layoutSettings.visibility = View.GONE
            binding.buttonSaveSettings.visibility = View.GONE

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayoutInfo, InfoRouter().fragment(InfoViewType.STREAMER))
            transaction.disallowAddToBackStack()
            transaction.commit()
        }
    }

    private fun checkEnableSave() {
        binding.buttonSaveSettings.enable(viewModel.checkEnableSave(this))
    }


}