package com.mouredev.twitimer.usecases.user

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ActivityUserBinding
import com.mouredev.twitimer.model.domain.User
import com.mouredev.twitimer.util.extension.titleLogo

class UserActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivityUserBinding

    private lateinit var viewModel: UserViewModel

    private var user: User? = null

    // Initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)

        // Content
        setContentView(binding.root)

        // Extra
        user = UserRouter.user(intent)

        // View Model
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Setup
        setup()
        data()
    }

    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out)
    }

    // Private

    private fun setup() {

        // UI

        supportActionBar?.titleLogo(this)
        supportActionBar?.elevation = 0f

        val backIcon = (ContextCompat.getDrawable(this, R.drawable.keyboard_arrow_left) as BitmapDrawable).bitmap
        val resizedCloseIcon: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(backIcon, 48, 48, false))
        resizedCloseIcon.setTint(ContextCompat.getColor(this, R.color.light))
        supportActionBar?.setHomeAsUpIndicator(resizedCloseIcon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun data() {

        com.mouredev.twitimer.usecases.account.user.UserRouter().replace(supportFragmentManager, R.id.userContainer, user, null)
    }

}