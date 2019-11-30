package io.github.umatoma.multiwebmediaviewer.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import io.github.umatoma.multiwebmediaviewer.R
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, HomeActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabSelected(tab: TabLayout.Tab) {
                setFragment(tab.position)
            }
        })

        setFragment(tabLayout.selectedTabPosition)
    }

    private fun setFragment(position: Int) {
        when (position) {
            0 -> {
                supportFragmentManager.beginTransaction().also {
                    it.replace(R.id.fragmentContainer, HatenaEntryListFragment())
                    it.commit()
                }
            }
            1 -> {
                supportFragmentManager.beginTransaction().also {
                    it.replace(R.id.fragmentContainer, FeedlyEntryListFragment())
                    it.commit()
                }
            }
            2 -> {
                supportFragmentManager.beginTransaction().also {
                    it.replace(R.id.fragmentContainer, SettingsFragment())
                    it.commit()
                }
            }
        }
    }
}
