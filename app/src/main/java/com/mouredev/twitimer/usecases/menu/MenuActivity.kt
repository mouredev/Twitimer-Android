package com.mouredev.twitimer.usecases.menu

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ActivityMenuBinding
import com.mouredev.twitimer.usecases.onboarding.OnboardingRouter
import com.mouredev.twitimer.util.FontSize
import com.mouredev.twitimer.util.FontType
import com.mouredev.twitimer.util.extension.font
import com.mouredev.twitimer.util.extension.navigation

class MenuActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivityMenuBinding

    private lateinit var viewModel: MenuViewModel

    // Initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)

        // Content
        setContentView(binding.root)

        // View Model
        viewModel = ViewModelProvider(this).get(MenuViewModel::class.java)

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

        binding.textViewBy.text = getString(viewModel.byText)
        binding.textViewInfo.text = getString(viewModel.infoText)
        binding.buttonSite.text = getString(viewModel.siteText)
        binding.buttonOnboarding.text = getString(viewModel.onboardingText)
        binding.textViewVersion.text = viewModel.versionText(this)
    }

    private fun setup() {

        // UI

        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f

        val closeIcon = (ContextCompat.getDrawable(this, R.drawable.close) as BitmapDrawable).bitmap
        val resizedCloseIcon: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(closeIcon, 48, 48, false))
        resizedCloseIcon.setTint(ContextCompat.getColor(this, R.color.light))
        supportActionBar?.setHomeAsUpIndicator(resizedCloseIcon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.textViewBy.font(FontSize.BODY, FontType.LIGHT, getColor(R.color.light))
        binding.textViewInfo.font(FontSize.CAPTION, FontType.LIGHT, getColor(R.color.light))
        binding.buttonSite.font(FontSize.BODY, FontType.LIGHT, getColor(R.color.text))
        binding.buttonOnboarding.font(FontSize.BODY, FontType.LIGHT, getColor(R.color.text))
        binding.textViewVersion.font(FontSize.CAPTION, FontType.LIGHT, getColor(R.color.text))

        buttons()
    }

    private fun buttons() {

        binding.imageButtonTwitch.setOnClickListener {
            viewModel.open(this, Network.TWITCH)
        }

        binding.imageButtonDiscord.setOnClickListener {
            viewModel.open(this, Network.DISCORD)
        }

        binding.imageButtonYouTube.setOnClickListener {
            viewModel.open(this, Network.YOUTUBE)
        }

        binding.imageButtonTwitter.setOnClickListener {
            viewModel.open(this, Network.TWITTER)
        }

        binding.imageButtonInstagram.setOnClickListener {
            viewModel.open(this, Network.INSTAGRAM)
        }

        binding.imageButtonGitHub.setOnClickListener {
            viewModel.open(this, Network.GITHUB)
        }

        binding.buttonSite.navigation {
            viewModel.openSite(this)
        }

        binding.buttonOnboarding.navigation {
            OnboardingRouter().launch(this)
        }
    }

}