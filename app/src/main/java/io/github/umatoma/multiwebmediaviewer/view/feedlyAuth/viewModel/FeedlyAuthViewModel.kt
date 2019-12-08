package io.github.umatoma.multiwebmediaviewer.view.feedlyAuth.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.iki.elonen.NanoHTTPD
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI

class FeedlyAuthViewModel(
    private val feedlyRepository: FeedlyRepository
) : ViewModel() {

    class Factory(val context: Context) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(FeedlyRepository::class.java)
                .newInstance(FeedlyRepository.Factory(context).create())
        }
    }

    private class LocalCallbackServer(port: Int) : NanoHTTPD(port) {

        override fun serve(session: IHTTPSession): Response {
            val queryParams = session.parms
            val html = buildString {
                append("<html>")
                append("  <meta charset=\"utf-8\">")
                append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                append("  <style>")
                append("    body { text-align: center; padding-top: 64px; }")
                append("    pre { overflow: auto; white-space: pre-wrap; word-wrap: break-word; }")
                append("    pre { padding: 8px 16px; background: #ddd; }")
                append("  </style>")
                append("  <body>")
                append("    <h4>MultiWebMediaViewer</h4>")
                append("    <h2>アクセスを許可しました</h2>")
                append("    <p>以下のコードを<br/>アプリケーションに入力して下さい。</p>")
                append("    <pre>${queryParams["code"]}</pre>")
                append("  </body>")
                append("</html>")
            }
            return NanoHTTPD.newFixedLengthResponse(html)
        }
    }

    private val localCallbackServer: LocalCallbackServer by lazy {
        val port = URI.create(feedlyRepository.getRedirectUrl()).port
        LocalCallbackServer(
            port
        )
    }

    val exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()
    val accessTokenLiveData: MutableLiveData<FeedlyAccessToken> = MutableLiveData()

    fun startLocalCallbackServer() {
        localCallbackServer.start()
    }

    fun stopLocalCallbackServer() {
        localCallbackServer.stop()
    }

    fun getAuthenticationUrl(): String {
        return feedlyRepository.getAuthenticationUrl()
    }

    fun fetchAccessToken(code: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val accessToken = feedlyRepository.getAccessToken(code)
                feedlyRepository.putAccessToken(accessToken)
                accessTokenLiveData.postValue(accessToken)
            } catch (e: Exception) {
                exceptionLiveData.postValue(e)
            }
        }
    }
}