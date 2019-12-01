package io.github.umatoma.multiwebmediaviewer.hatenaEntry

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
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

    @SuppressLint("SetJavaScriptEnabled", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hatena_entry)

        val entry = intent.getSerializableExtra(KEY_HATENA_ENTRY) as HatenaEntry

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

        bottomNavigationHatenaEntry
            .getOrCreateBadge(R.id.menuHatenaEntryComment).number = entry.count
    }
}
