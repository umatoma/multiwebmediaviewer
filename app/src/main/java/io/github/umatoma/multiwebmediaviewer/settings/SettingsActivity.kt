package io.github.umatoma.multiwebmediaviewer.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.home.HomeActivity

class SettingsActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

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
}