package io.github.umatoma.multiwebmediaviewer.hatenaEntry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.HatenaEntry
import kotlinx.android.synthetic.main.activity_hatena_entry.*

class HatenaEntryActivity : AppCompatActivity() {

    companion object {

        private const val KEY_HATENA_ENTRY = "KEY_HATENA_ENTRY"

        fun startActivity(context: Context, entry: HatenaEntry) {
            val intent = Intent(context, HatenaEntryActivity::class.java)
            intent.putExtra(KEY_HATENA_ENTRY, entry)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hatena_entry)

        val entry = intent.getSerializableExtra(KEY_HATENA_ENTRY) as HatenaEntry
        val entryFragment = HatenaEntryFragment.newInstance(entry)
        val bookmarkListFragment = HatenaBookmarkListFragment.newInstance(entry)

        supportActionBar?.let {
            it.title = entry.title
            it.subtitle = entry.getUrlHost()
            it.setDisplayHomeAsUpEnabled(true)
        }

        bottomNavigationHatenaEntry.let {
            it.getOrCreateBadge(R.id.menuHatenaEntryComment).number = entry.count
            it.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menuHatenaEntryBookmark -> {
                        supportFragmentManager.beginTransaction().also { transition ->
                            transition.show(entryFragment)
                            transition.hide(bookmarkListFragment)
                            transition.commit()
                        }
                        return@setOnNavigationItemSelectedListener true
                    }
                    R.id.menuHatenaEntryComment -> {
                        supportFragmentManager.beginTransaction().also { transition ->
                            transition.hide(entryFragment)
                            transition.show(bookmarkListFragment)
                            transition.commit()
                        }
                        return@setOnNavigationItemSelectedListener true
                    }
                    else -> {
                        return@setOnNavigationItemSelectedListener false
                    }
                }
            }
        }

        supportFragmentManager.beginTransaction().also {
            it.replace(R.id.layoutHatenaEntryContent, entryFragment)
            it.add(R.id.layoutHatenaEntryContent, bookmarkListFragment)
            it.hide(bookmarkListFragment)
            it.commit()
        }
    }
}
