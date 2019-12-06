package io.github.umatoma.multiwebmediaviewer.view.feedlyAuth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import fi.iki.elonen.NanoHTTPD
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRemoteRepository
import io.github.umatoma.multiwebmediaviewer.view.home.HomeActivity
import io.github.umatoma.multiwebmediaviewer.viewModel.feedlyAuth.FeedlyAuthViewModel
import kotlinx.android.synthetic.main.activity_feedly_auth.*

class FeedlyAuthActivity : AppCompatActivity() {

    companion object {

        fun startActivity(context: Context) {
            context.startActivity(Intent(context, FeedlyAuthActivity::class.java))
        }
    }

    private lateinit var viewModel: FeedlyAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedly_auth)

        val viewModelFactory = FeedlyAuthViewModel.Factory(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FeedlyAuthViewModel::class.java)

        btnConfirmFeedlyAuthCode.setOnClickListener {
            val authCode = editTxtFeedlyAuthCode.text.toString()
            viewModel.fetchAccessToken(authCode)
        }

        viewModel.exceptionLiveData.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })
        viewModel.accessTokenLiveData.observe(this, Observer {
            HomeActivity.startActivity(this)
            finish()
        })

        viewModel.startLocalCallbackServer()

        val url = Uri.parse(viewModel.getAuthenticationUrl())
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, url)
    }

    override fun onDestroy() {
        viewModel.stopLocalCallbackServer()

        super.onDestroy()
    }
}
