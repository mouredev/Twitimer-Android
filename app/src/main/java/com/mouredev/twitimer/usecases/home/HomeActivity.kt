package com.mouredev.twitimer.usecases.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.mouredev.twitimer.R
import com.mouredev.twitimer.databinding.ActivityHomeBinding
import com.mouredev.twitimer.model.session.Session
import com.mouredev.twitimer.usecases.account.account.AccountFragmentListener
import com.mouredev.twitimer.usecases.account.account.AccountRouter
import com.mouredev.twitimer.usecases.countdown.CountdownFragmentListener
import com.mouredev.twitimer.usecases.countdown.CountdownRouter
import com.mouredev.twitimer.usecases.menu.MenuRouter
import com.mouredev.twitimer.usecases.onboarding.OnboardingRouter
import com.mouredev.twitimer.usecases.search.SearchRouter
import com.mouredev.twitimer.util.extension.titleLogo


class HomeActivity : AppCompatActivity(), AccountFragmentListener, CountdownFragmentListener {

    // Properties

    private lateinit var binding: ActivityHomeBinding

    private lateinit var viewModel: HomeViewModel

    private var countdownFragment: CountdownRouter? = null
    private var searchFragment: SearchRouter? = null
    private var accountFragment: AccountRouter? = null

    private var selectedItem: Int = -1

    // Initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)

        // Content
        setContentView(binding.root)

        // View Model
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Setup
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.home_menu) {
            MenuRouter().launch(this)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    // Private

    private fun setup() {

        // UI

        supportActionBar?.titleLogo(this)
        supportActionBar?.elevation = 0f

        onboarding()
        loadTabs()
        defaultTab()
    }

    private fun onboarding() {

        if (viewModel.onboarding(this)) {
            OnboardingRouter().launch(this)
        }
    }

    private fun loadTabs() {

        // Fragments
        countdownFragment?.let { countdownFragment ->
            countdownFragment.remove(supportFragmentManager)
        }
        searchFragment?.let { searchFragment ->
            searchFragment.remove(supportFragmentManager)
        }
        accountFragment?.let { accountFragment ->
            accountFragment.remove(supportFragmentManager)
        }

        countdownFragment = CountdownRouter()
        searchFragment = SearchRouter()
        accountFragment = AccountRouter()


        binding.homeBottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->

            if (selectedItem != menuItem.itemId) {

                selectedItem = menuItem.itemId

                when (menuItem.itemId) {
                    R.id.home_menu_countdown -> {
                        searchFragment?.hide(supportFragmentManager)
                        accountFragment?.hide(supportFragmentManager)
                        if (supportFragmentManager.findFragmentByTag(R.id.home_menu_countdown.toString()) == null) {
                            countdownFragment?.add(supportFragmentManager, R.id.homeContainer, R.id.home_menu_countdown.toString(), this)
                        }
                        countdownFragment?.show(supportFragmentManager)
                    }
                    R.id.home_menu_search -> {
                        countdownFragment?.hide(supportFragmentManager)
                        accountFragment?.hide(supportFragmentManager)
                        if (supportFragmentManager.findFragmentByTag(R.id.home_menu_search.toString()) == null) {
                            searchFragment?.add(supportFragmentManager, R.id.homeContainer, R.id.home_menu_search.toString())
                        }
                        searchFragment?.show(supportFragmentManager)
                    }
                    R.id.home_menu_account -> {
                        countdownFragment?.hide(supportFragmentManager)
                        searchFragment?.hide(supportFragmentManager)
                        if (supportFragmentManager.findFragmentByTag(R.id.home_menu_account.toString()) == null) {
                            accountFragment?.add(supportFragmentManager, R.id.homeContainer, R.id.home_menu_account.toString(), this)
                        }
                        accountFragment?.show(supportFragmentManager)
                    }
                }
                true
            } else {
                false
            }
        }
    }

    private fun defaultTab() {
        binding.homeBottomNavigationView.selectedItemId = viewModel.defaultTab()
    }

    // AccountFragmentListener

    override fun authenticated() {

        Session.instance.reloadUser(this) {

        }
    }

    // CountdownFragmentListener

    override fun showSearch() {
        binding.homeBottomNavigationView.selectedItemId = viewModel.searchTab()
    }

}