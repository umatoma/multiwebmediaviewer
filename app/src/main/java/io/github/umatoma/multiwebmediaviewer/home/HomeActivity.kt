package io.github.umatoma.multiwebmediaviewer.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    companion object {

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    private lateinit var viewModel: HomeViewModel
    private lateinit var hatenaEntryListFragment: HatenaEntryListContainerFragment
    private lateinit var feedlyEntryListFragment: FeedlyEntryListFragment
    private lateinit var settingsFragment: SettingsFragment
    private lateinit var fragmentArray: Array<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val viewModelFactory = HomeViewModel.Factory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HomeViewModel::class.java)

        hatenaEntryListFragment = HatenaEntryListContainerFragment()
        feedlyEntryListFragment = FeedlyEntryListFragment()
        settingsFragment = SettingsFragment()

        fragmentArray = arrayOf(
            hatenaEntryListFragment,
            feedlyEntryListFragment,
            settingsFragment
        )

        bottomNavigationHome.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menuHatenaEntryList -> {
                    showHatenaEntryListFragment()
                }
                R.id.menuFeelyEntryList -> {
                    showFeedlyEntryListFragment()
                }
                R.id.menuSettings -> {
                    showSettingsFragment()
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        initAndShowHatenaEntryFragment()
    }

    override fun onResume() {
        super.onResume()

        viewModel.fetchIsSignedInAny()
    }

    private fun showHatenaEntryListFragment() {
        showFragment(
            hatenaEntryListFragment,
            hatenaEntryListFragment.getTitle(this)
        )
    }

    private fun showFeedlyEntryListFragment() {
        showFragment(
            feedlyEntryListFragment,
            feedlyEntryListFragment.getTitle(this)
        )
    }

    private fun showSettingsFragment() {
        showFragment(
            settingsFragment,
            settingsFragment.getTitle(this)
        )
    }

    private fun initAndShowHatenaEntryFragment() {
        supportFragmentManager.beginTransaction().also {
            for (fragment in fragmentArray) {
                it.add(R.id.layoutHomeContent, fragment)
            }
            it.commit()
        }

        showHatenaEntryListFragment()
    }

    private fun showFragment(targetFragment: Fragment, title: String) {
        supportFragmentManager.beginTransaction().also {
            for (fragment in fragmentArray) {
                if (targetFragment === fragment) {
                    it.show(fragment)
                } else {
                    it.hide(fragment)
                }
            }
            it.commit()
        }

        setTitle(title)
    }

}
