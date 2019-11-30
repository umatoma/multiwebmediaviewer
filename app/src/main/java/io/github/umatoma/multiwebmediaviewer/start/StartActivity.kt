package io.github.umatoma.multiwebmediaviewer.start

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.umatoma.multiwebmediaviewer.R
import io.github.umatoma.multiwebmediaviewer.home.HomeActivity


class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        HomeActivity.startActivity(this)
        finish()
    }
}
