package io.github.umatoma.multiwebmediaviewer.viewModel.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaLocalRepository

class HomeViewModel(
    private val hatenaLocalRepository: HatenaLocalRepository
): ViewModel() {

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val hatenaLocalRepository = HatenaLocalRepository(context)

            return modelClass
                .getConstructor(
                    HatenaLocalRepository::class.java
                )
                .newInstance(
                    hatenaLocalRepository
                )
        }
    }

    val isSignedInHatenaLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun signOutHatena() {
        hatenaLocalRepository.signOut()
        fetchIsSignedInHatena()
    }

    fun fetchIsSignedInAny() {
        fetchIsSignedInHatena()
    }

    private fun fetchIsSignedInHatena() {
        isSignedInHatenaLiveData.postValue(hatenaLocalRepository.isSignedIn())
    }
}