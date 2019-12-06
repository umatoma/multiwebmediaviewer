package io.github.umatoma.multiwebmediaviewer.view.hatenaAuth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.view.home.HomeActivity
import io.github.umatoma.multiwebmediaviewer.viewModel.hatenaAuth.HatenaAuthViewModel
import kotlinx.android.synthetic.main.activity_hatena_auth.*

class HatenaAuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hatena_auth)

        val viewModelFactory = HatenaAuthViewModel.Factory(applicationContext)
        val viewModel = ViewModelProviders
            .of(this, viewModelFactory)
            .get(HatenaAuthViewModel::class.java)

        viewModel.exceptionLiveData.observe(this, Observer {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        })
        viewModel.authenticationUrlLiveData.observe(this, Observer {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it)))
        })
        viewModel.accessTokenLiveData.observe(this, Observer {
            HomeActivity.startActivity(this)
            finish()
        })

        btnConfirmHatenaVerifier.setOnClickListener {
            val verifier = editTxtHatenaVerifier.text.toString()
            viewModel.fetchAccessToken(verifier)
        }

        viewModel.fetchAuthenticationUrl()
    }
}

