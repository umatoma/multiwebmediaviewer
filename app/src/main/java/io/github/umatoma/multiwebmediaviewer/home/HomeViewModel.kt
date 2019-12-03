package io.github.umatoma.multiwebmediaviewer.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.common.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.common.hatena.repository.HatenaLocalRepository
import io.github.umatoma.multiwebmediaviewer.common.hatena.repository.HatenaRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val hatenaLocalRepository: HatenaLocalRepository,
    private val hatenaRemoteRepository: HatenaRemoteRepository
): ViewModel() {

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val hatenaLocalRepository = HatenaLocalRepository(context)
            val hatenaRemoteRepository = HatenaRemoteRepository(hatenaLocalRepository)
            return modelClass
                .getConstructor(HatenaLocalRepository::class.java, HatenaRemoteRepository::class.java)
                .newInstance(hatenaLocalRepository, hatenaRemoteRepository)
        }
    }

    val isSignedInHatenaLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val hatenaEntryListLiveData: MutableLiveData<List<HatenaEntry>> = MutableLiveData()

    fun fetchIsSignedInHatena() {
        isSignedInHatenaLiveData.postValue(hatenaLocalRepository.isSignedIn())
    }

    fun signOutHatena() {
        hatenaLocalRepository.signOut()
        fetchIsSignedInHatena()
    }

    fun fetchIsSignedInAny() {
        fetchIsSignedInHatena()
    }

    fun fetchHatenaEntryList() {
        CoroutineScope(Dispatchers.IO).launch {
            val entryList = hatenaRemoteRepository.getHotEntryList(HatenaEntry.Category.ALL)
            hatenaEntryListLiveData.postValue(entryList)
        }
    }
}