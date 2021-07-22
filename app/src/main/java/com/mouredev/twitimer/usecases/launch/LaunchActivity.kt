package com.mouredev.twitimer.usecases.launch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.mouredev.twitimer.databinding.ActivityLaunchBinding
import com.mouredev.twitimer.databinding.ActivityUserBinding
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.usecases.home.HomeRouter

class LaunchActivity : AppCompatActivity() {

    // Properties

    private lateinit var binding: ActivityLaunchBinding

    // Initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)

        // Content
        setContentView(binding.root)

        // Setup
        setup()
        data()
    }

    // Private

    private fun setup() {

        supportActionBar?.hide()

        // Remote notifications
        Firebase.messaging.isAutoInitEnabled = true
    }

    private fun data() {

        // Session
        Session.instance.configure(this)
        Session.instance.fullReloadUser(this) {
            showHome()
        }
    }

    private fun showHome() {

        HomeRouter().launch(this)
        finish()
    }

}