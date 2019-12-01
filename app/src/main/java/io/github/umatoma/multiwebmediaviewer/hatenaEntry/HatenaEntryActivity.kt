package io.github.umatoma.multiwebmediaviewer.hatenaEntry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

    @SuppressLint("SetJavaScriptEnabled", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hatena_entry)

        val entry = intent.getSerializableExtra(KEY_HATENA_ENTRY) as HatenaEntry
        val viewModelFactory = HatenaEntryViewModel.Factory(this, entry)
        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(HatenaEntryViewModel::class.java)

        supportActionBar?.let {
            it.title = entry.title
            it.subtitle = entry.getUrlHost()
            it.setDisplayHomeAsUpEnabled(true)
        }

        webViewHatenaEntry.let {
            it.webViewClient = object : WebViewClient() {}
            it.settings.javaScriptEnabled = true
            it.loadUrl(entry.url)
        }

        bottomNavigationHatenaEntry.let {
            it.getOrCreateBadge(R.id.menuHatenaEntryComment).number = entry.count
            it.setOnNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menuHatenaEntryBookmark -> {}
                    R.id.menuHatenaEntryComment -> {
                        viewModel.fetchCommentList()
                    }
                    R.id.menuHatenaEntryShare -> {}
                    R.id.menuHatenaEntryOpen -> {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(entry.url)))
                    }
                    else -> return@setOnNavigationItemSelectedListener false
                }
                return@setOnNavigationItemSelectedListener true
            }
        }

        viewModel.bookmarkListLiveData.observe(this, Observer { bookmarkList ->
            val msg = bookmarkList.map { it.comment }.filter { it != "" }.joinToString("\n")
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        })
    }
}
