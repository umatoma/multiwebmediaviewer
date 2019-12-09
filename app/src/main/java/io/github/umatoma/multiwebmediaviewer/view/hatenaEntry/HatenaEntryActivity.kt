package io.github.umatoma.multiwebmediaviewer.view.hatenaEntry

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.view.hatenaEntry.fragment.HatenaBookmarkListFragment
import io.github.umatoma.multiwebmediaviewer.view.hatenaEntry.fragment.HatenaEntryFragment
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

    private lateinit var entryFragment: HatenaEntryFragment
    private lateinit var bookmarkListFragment: HatenaBookmarkListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hatena_entry)

        val entry = intent.getSerializableExtra(KEY_HATENA_ENTRY) as HatenaEntry
        val viewModel = ViewModelProviders.of(this)
            .get(HatenaEntryViewModel::class.java)

        entryFragment = HatenaEntryFragment.newInstance(entry)
        bookmarkListFragment = HatenaBookmarkListFragment.newInstance(entry)

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
                        showEntryFragment()
                    }
                    R.id.menuHatenaEntryComment -> {
                        showBookmarkListFragment()
                    }
                    else -> {
                        return@setOnNavigationItemSelectedListener false
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }

        btnHatenaEntryOpenMenu.setOnClickListener {
            viewModel.toggleFloatingActionButton()
        }

        btnHatenaEntryShare.setOnClickListener {
            shareEntry(entry)
        }

        btnHatenaEntryOpenInBrowser.setOnClickListener {
            openEntryInBrowser(entry)
        }

        btnHatenaEntryBookmark.setOnClickListener {
            // TODO: Do bookmark
        }

        viewModel.isOpenFloatingActionButtonLiveData.observe(this, Observer {
            setFloatingActionButtonsView(it)
        })

        supportFragmentManager.beginTransaction().also {
            it.replace(R.id.layoutHatenaEntryContent, entryFragment)
            it.add(R.id.layoutHatenaEntryContent, bookmarkListFragment)
            it.hide(bookmarkListFragment)
            it.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showEntryFragment() {
        supportFragmentManager.beginTransaction().also { transition ->
            transition.show(entryFragment)
            transition.hide(bookmarkListFragment)
            transition.commit()
        }
    }

    private fun showBookmarkListFragment() {
        supportFragmentManager.beginTransaction().also { transition ->
            transition.hide(entryFragment)
            transition.show(bookmarkListFragment)
            transition.commit()
        }
    }

    private fun shareEntry(entry: HatenaEntry) {
        val sendIntent = Intent().also {
            it.action = Intent.ACTION_SEND
            it.putExtra(Intent.EXTRA_TEXT, "${entry.title}\n${entry.url}")
            it.type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun openEntryInBrowser(entry: HatenaEntry) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(entry.url)))
    }

    private fun setFloatingActionButtonsView(isOpen: Boolean) {
        if (isOpen) {
            btnHatenaEntryOpenMenu.setImageResource(R.drawable.ic_close)
            btnHatenaEntryShare.show()
            btnHatenaEntryOpenInBrowser.show()
            btnHatenaEntryBookmark.show()
        } else {
            btnHatenaEntryOpenMenu.setImageResource(R.drawable.ic_menu)
            btnHatenaEntryShare.hide()
            btnHatenaEntryOpenInBrowser.hide()
            btnHatenaEntryBookmark.hide()
        }
    }
}
