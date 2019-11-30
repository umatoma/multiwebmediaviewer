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
import kotlinx.coroutines.withContext

class HatenaEntryListViewModel(
    private val localRepository: HatenaLocalRepository,
    private val remoteRepository: HatenaRemoteRepository
) : ViewModel() {

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val localRepository = HatenaLocalRepository(context)
            val remoteRepository = HatenaRemoteRepository(localRepository)

            return modelClass
                .getConstructor(HatenaLocalRepository::class.java, HatenaRemoteRepository::class.java)
                .newInstance(localRepository, remoteRepository)
        }
    }

    val isSignedInAndHasChangedLiveData: MutableLiveData<Pair<Boolean, Boolean>> = MutableLiveData()
    val entryListLiveData: MutableLiveData<List<HatenaEntry>> = MutableLiveData()

    fun fetchIsSignedIn() {
        val prevIsSignedIn = isSignedInAndHasChangedLiveData.value?.first
        val currentIsSignedIn = localRepository.isSignedIn()
        val hasChanged = (currentIsSignedIn != prevIsSignedIn)

        isSignedInAndHasChangedLiveData.postValue(
            Pair(currentIsSignedIn, hasChanged)
        )
    }

    fun fetchEntryList() {
        CoroutineScope(Dispatchers.IO).launch {
            val entryList = remoteRepository.getHotEntryList(HatenaEntry.Category.ALL)
            entryListLiveData.postValue(entryList)
        }
    }

    fun clearEntryList() {
        entryListLiveData.postValue(mutableListOf())
    }

    suspend fun getHatenaUser() = withContext(Dispatchers.IO) {
        return@withContext remoteRepository.getUser()
    }
}