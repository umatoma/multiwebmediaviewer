package io.github.umatoma.multiwebmediaviewer.view.feedlyAuth.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.model.common.OAuth2LocalCallbackServer
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyAccessToken
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI

class FeedlyAuthViewModel(
    private val feedlyRepository: FeedlyRepository,
    private val localCallbackServer: OAuth2LocalCallbackServer
) : ViewModel() {

    class Factory(val context: Context) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val feedlyRepository = FeedlyRepository.Factory(context).create()
            val port = URI.create(feedlyRepository.getRedirectUrl()).port
            return modelClass
                .getConstructor(FeedlyRepository::class.java, OAuth2LocalCallbackServer::class.java)
                .newInstance(feedlyRepository, OAuth2LocalCallbackServer(port))
        }
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

    fun fetchAccessToken(code: String) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val accessToken = feedlyRepository.getAccessToken(code)
            feedlyRepository.putAccessToken(accessToken)
            accessTokenLiveData.postValue(accessToken)
        } catch (e: Exception) {
            exceptionLiveData.postValue(e)
        }
    }
}