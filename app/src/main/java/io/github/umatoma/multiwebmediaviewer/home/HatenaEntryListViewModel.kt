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

class HatenaEntryListViewModel(
    private val entryType: HatenaEntry.Type,
    private val entryCategory: HatenaEntry.Category,
    private val hatenaRemoteRepository: HatenaRemoteRepository
) : ViewModel() {

    class Factory(
        private val entryType: HatenaEntry.Type,
        private val entryCategory: HatenaEntry.Category,
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val hatenaLocalRepository = HatenaLocalRepository(context)
            val hatenaRemoteRepository = HatenaRemoteRepository(hatenaLocalRepository)

            return modelClass
                .getConstructor(
                    HatenaEntry.Type::class.java,
                    HatenaEntry.Category::class.java,
                    HatenaRemoteRepository::class.java
                )
                .newInstance(
                    entryType,
                    entryCategory,
                    hatenaRemoteRepository
                )
        }
    }

    val isFetchingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val hatenaEntryListLiveData: MutableLiveData<List<HatenaEntry>> = MutableLiveData()

    fun fetchHatenaEntryList() {
        CoroutineScope(Dispatchers.IO).launch {
            val entryList = when (entryType) {
                HatenaEntry.Type.HOT -> {
                    hatenaRemoteRepository.getHotEntryList(entryCategory)
                }
                HatenaEntry.Type.NEW -> {
                    hatenaRemoteRepository.getNewEntryList(entryCategory)
                }
            }
            isFetchingLiveData.postValue(false)
            hatenaEntryListLiveData.postValue(entryList)
        }
        isFetchingLiveData.postValue(true)
    }
}