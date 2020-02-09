package io.github.umatoma.multiwebmediaviewer.view.home.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
        val prefFeedlyAccessToken = findPreference<Preference>("_FEEDLY_ACCESS_TOKEN")

        prefHatenaSignOut?.setOnPreferenceClickListener {
            homeViewModel.signOutHatena()
            return@setOnPreferenceClickListener true
        }

        prefFeedlySignOut?.setOnPreferenceClickListener {
            homeViewModel.signOutFeedly()
            return@setOnPreferenceClickListener true
        }

        prefFeedlyAccessToken?.setOnPreferenceClickListener {
            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("", it.summary)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "コピーしました", Toast.LENGTH_SHORT).show()

            return@setOnPreferenceClickListener true
        }

        homeViewModel.isSignedInHatenaLiveData.observe(viewLifecycleOwner, Observer {
            prefHatenaSignOut?.isEnabled = it
        })

        homeViewModel.isSignedInFeedlyLiveData.observe(viewLifecycleOwner, Observer { isSignedIn ->
            prefFeedlySignOut?.isEnabled = isSignedIn
            prefFeedlyAccessToken?.isEnabled = isSignedIn

            if (isSignedIn) {
                homeViewModel.fetchFeedlyAccessToken()
            }
        })

        homeViewModel.feedlyAccessTokenLiveData.observe(viewLifecycleOwner, Observer {
            prefFeedlyAccessToken?.setSummary(it.accessToken)
        })
    }
}