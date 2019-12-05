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
    private val entryKind: HatenaEntry.Kind,
    private val entryCategory: HatenaEntry.Category,
    private val hatenaRemoteRepository: HatenaRemoteRepository
) : ViewModel() {

    class Factory(
        private val entryKind: HatenaEntry.Kind,
        private val entryCategory: HatenaEntry.Category,
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val hatenaLocalRepository = HatenaLocalRepository(context)
            val hatenaRemoteRepository = HatenaRemoteRepository(hatenaLocalRepository)

            return modelClass
                .getConstructor(
                    HatenaEntry.Kind::class.java,
                    HatenaEntry.Category::class.java,
                    HatenaRemoteRepository::class.java
                )
                .newInstance(
                    entryKind,
                    entryCategory,
                    hatenaRemoteRepository
                )
        }
    }

    val isFetchingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val hatenaEntryListLiveData: MutableLiveData<List<HatenaEntry>> = MutableLiveData()

    private var entryListPageNumber: Int = 1;

    fun fetchHatenaEntryList(pageNumber: Int = 1) {
        CoroutineScope(Dispatchers.IO).launch {
            val entryList = when (entryKind) {
                HatenaEntry.Kind.HOT -> {
                    hatenaRemoteRepository.getHotEntryList(entryCategory, pageNumber)
                }
                HatenaEntry.Kind.NEW -> {
                    hatenaRemoteRepository.getNewEntryList(entryCategory, pageNumber)
                }
            }

            isFetchingLiveData.postValue(false)
            entryListPageNumber = pageNumber

            if (entryListPageNumber == 1) {
                hatenaEntryListLiveData.postValue(entryList)
            } else {
                hatenaEntryListLiveData.postValue(hatenaEntryListLiveData.value!! + entryList)
            }
        }
        isFetchingLiveData.postValue(true)
    }

    fun fetchHatenaEntryListOnNextPage() {
        fetchHatenaEntryList(entryListPageNumber + 1)
    }
}