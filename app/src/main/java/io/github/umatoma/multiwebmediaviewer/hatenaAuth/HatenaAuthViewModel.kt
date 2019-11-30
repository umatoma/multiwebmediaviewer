package io.github.umatoma.multiwebmediaviewer.hatenaAuth

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.HatenaAccessToken
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.HatenaRequestToken
import io.github.umatoma.multiwebmediaviewer.common.hatena.repository.HatenaLocalRepository
import io.github.umatoma.multiwebmediaviewer.common.hatena.repository.HatenaRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HatenaAuthViewModel(
    private val remoteRepository: HatenaRemoteRepository,
    private val localRepository: HatenaLocalRepository
) : ViewModel() {

    class Factory(
        private val context: Context
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val localRepository = HatenaLocalRepository(context)
            val remoteRepository = HatenaRemoteRepository(localRepository)

            return modelClass
                .getConstructor(HatenaRemoteRepository::class.java, HatenaLocalRepository::class.java)
                .newInstance(remoteRepository, localRepository)
        }
    }

    var exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()
    var authenticationUrlLiveData: MutableLiveData<String> = MutableLiveData()
    var accessTokenLiveData: MutableLiveData<HatenaAccessToken> = MutableLiveData()

    private var requestToken: HatenaRequestToken? = null

    fun fetchAuthenticationUrl() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = remoteRepository.getRequestToken()
                val authUrl = remoteRepository.getAuthenticationUrl(token)

                requestToken = token

                authenticationUrlLiveData.postValue(authUrl)
            } catch (e: Exception) {
                exceptionLiveData.postValue(e)
            }
        }
    }

    fun fetchAccessToken(verifier: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = requestToken ?: throw Exception("Failed to fetch access token.")
                val accessToken = remoteRepository.getAccessToken(token, verifier)

                localRepository.putAccessToken(accessToken)
                accessTokenLiveData.postValue(accessToken)
            } catch (e: Exception) {
                exceptionLiveData.postValue(e)
            }
        }
    }
}