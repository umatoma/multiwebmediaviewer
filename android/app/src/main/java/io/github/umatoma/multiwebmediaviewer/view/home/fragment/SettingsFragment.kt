package io.github.umatoma.multiwebmediaviewer.view.home.fragment

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.view.home.viewModel.HomeViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    fun getTitle(context: Context): String {
        return context.getString(R.string.fragment_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val homeViewModel = HomeViewModel.Factory(requireContext()).create(requireActivity())

        val prefHatenaSignOut = findPreference<Preference>(
            getString(R.string.fragment_settings_key_hatena_sign_out)
        )
        val prefFeedlySignOut = findPreference<Preference>(
            getString(R.string.fragment_settings_key_feedly_sign_out)
        )

        prefHatenaSignOut?.setOnPreferenceClickListener {
            homeViewModel.signOutHatena()
            return@setOnPreferenceClickListener true
        }

        prefFeedlySignOut?.setOnPreferenceClickListener {
            homeViewModel.signOutFeedly()
            return@setOnPreferenceClickListener true
        }

        homeViewModel.isSignedInHatenaLiveData.observe(viewLifecycleOwner, Observer {
            prefHatenaSignOut?.isEnabled = it
        })

        homeViewModel.isSignedInFeedlyLiveData.observe(viewLifecycleOwner, Observer {
            prefFeedlySignOut?.isEnabled = it
        })
    }
}