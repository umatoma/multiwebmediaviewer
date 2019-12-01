package io.github.umatoma.multiwebmediaviewer.home

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.umatoma.multiwebmediaviewer.R

class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

            requireActivity().title = getString(R.string.fragment_settings_title)

            val viewModelFactory = SettingsViewModel.Factory(requireContext())
            val viewModel = ViewModelProviders
                .of(this, viewModelFactory)
                .get(SettingsViewModel::class.java)

            val prefHatenaSignOut = findPreference<Preference>("hatenaSignOut")
            prefHatenaSignOut?.setOnPreferenceClickListener {
                viewModel.signOutHatena()
                return@setOnPreferenceClickListener true
            }

            viewModel.isSignedInHatenaLiveData.observe(viewLifecycleOwner, Observer {
                prefHatenaSignOut?.isEnabled = it
            })
        }
    }