package io.github.umatoma.multiwebmediaviewer.view.hatenaAuth.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaAccessToken
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaRequestToken
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HatenaAuthViewModel(
    private val hatenaRepository: HatenaRepository
) : ViewModel() {

    class Factory(
        private val context: Context
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(HatenaRepository::class.java)
                .newInstance(HatenaRepository.Factory(context).create())
        }
    }

    val exceptionLiveData: MutableLiveData<Exception> = MutableLiveData()
    val authenticationUrlLiveData: MutableLiveData<String> = MutableLiveData()
    val accessTokenLiveData: MutableLiveData<HatenaAccessToken> = MutableLiveData()

    private var requestToken: HatenaRequestToken? = null

    fun fetchAuthenticationUrl() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = hatenaRepository.getRequestToken()
                val authUrl = hatenaRepository.getAuthenticationUrl(token)

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
                val accessToken = hatenaRepository.getAccessToken(token, verifier)

                hatenaRepository.putAccessToken(accessToken)
                accessTokenLiveData.postValue(accessToken)
            } catch (e: Exception) {
                exceptionLiveData.postValue(e)
            }
        }
    }
}