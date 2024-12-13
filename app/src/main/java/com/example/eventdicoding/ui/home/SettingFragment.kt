package com.example.eventdicoding.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import com.example.eventdicoding.R
import com.example.eventdicoding.databinding.FragmentSettingBinding
import com.example.eventdicoding.ui.model.MainViewModel
import com.example.eventdicoding.ui.model.ViewModelFactory
import com.example.eventdicoding.ui.setting.SettingPreferences
import com.example.eventdicoding.ui.setting.dataStore

class SettingFragment : Fragment(R.layout.fragment_setting) {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSettingBinding.inflate(inflater, container, false)

        val switchTheme = binding.switchTheme

        val pref = SettingPreferences.getInstance(requireContext().dataStore)
        val mainViewModel: MainViewModel by viewModels { ViewModelFactory(pref) }

        mainViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.isChecked = false
            }
        }

        switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            Handler(Looper.getMainLooper()).postDelayed({
                mainViewModel.saveThemeSetting(isChecked)
            }, 1000)
        }

        return binding.root
    }
}
