package io.github.umatoma.multiwebmediaviewer.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.common.hatena.repository.HatenaLocalRepository

class SettingsViewModel(
    private val hatenaLocalRepository: HatenaLocalRepository
) : ViewModel() {

    val isSignedInHatenaLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData(hatenaLocalRepository.isSignedIn())
    }

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val hatenaLocalRepository = HatenaLocalRepository(context)

            return modelClass
                .getConstructor(HatenaLocalRepository::class.java)
                .newInstance(hatenaLocalRepository)
        }
    }

    fun signOutHatena() {
        hatenaLocalRepository.signOut()
        isSignedInHatenaLiveData.postValue(hatenaLocalRepository.isSignedIn())
    }
}