package com.example.eventdicoding.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.eventdicoding.R
import com.example.eventdicoding.databinding.ActivityMainBinding
import com.example.eventdicoding.ui.home.EventFragment
import com.example.eventdicoding.ui.home.FavoriteFragment
import com.example.eventdicoding.ui.home.HomeFragment
import com.example.eventdicoding.ui.home.SettingFragment
import com.example.eventdicoding.ui.model.MainViewModel
import com.example.eventdicoding.ui.model.ViewModelFactory
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore
import com.google.android.material.search.SearchBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = SettingPreferences.getInstance(application.dataStore)
        val mainViewModel: MainViewModel by viewModels { ViewModelFactory(pref) }
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing && !isDestroyed) {
                binding = ActivityMainBinding.inflate(layoutInflater)
                setContentView(binding.root)

                setupSearchBar()
                supportActionBar?.hide()
                loadFragment(HomeFragment())

                binding.bottomNavigation.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.navigation_home -> {
                            loadFragment(HomeFragment())
                            true
                        }
                        R.id.navigation_upcoming -> {
                            loadFragment(EventFragment.newInstance(1))
                            true
                        }
                        R.id.navigation_finished -> {
                            loadFragment(EventFragment.newInstance(0))
                            true
                        }
                        R.id.navigation_favorite -> {
                            loadFragment(FavoriteFragment())
                            true
                        }
                        R.id.navigation_setting -> {
                            loadFragment(SettingFragment())
                            true
                        }
                        else -> false
                    }
                }
            }
        }, 300)

    }

    private fun loadFragment(fragment: Fragment) {
        if (!isFinishing && !isDestroyed) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            when (fragment) {
                is SettingFragment -> {
                    findViewById<SearchBar>(R.id.searchBar).visibility = View.GONE
                }
                is FavoriteFragment -> {
                    findViewById<SearchBar>(R.id.searchBar).visibility = View.GONE
                }
                else -> {
                    findViewById<SearchBar>(R.id.searchBar).visibility = View.VISIBLE
                }
            }
        }
    }


    private fun setupSearchBar() {
        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    searchInCurrentFragment(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val query = binding.searchView.text.toString().trim()
            if (query.isNotEmpty()) {
                binding.searchBar.setText(query)
                binding.searchView.hide()

                searchInCurrentFragment(query)

                binding.searchBar.setText("")
                binding.searchView.editText.setText("")
            }
            false
        }
    }

    private fun searchInCurrentFragment(query: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        when (currentFragment) {
            is HomeFragment -> {
                currentFragment.search(query)
            }
            is EventFragment -> {
                currentFragment.search(query)
            }
            else -> {
                Toast.makeText(this, "No search functionality in the current screen", Toast.LENGTH_SHORT).show()
            }
        }
    }
}