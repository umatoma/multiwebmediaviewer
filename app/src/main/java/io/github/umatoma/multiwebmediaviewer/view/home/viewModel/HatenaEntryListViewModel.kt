package io.github.umatoma.multiwebmediaviewer.view.home.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HatenaEntryListViewModel(
    private val entryKind: HatenaEntry.Kind,
    private val entryCategory: HatenaEntry.Category,
    private val hatenaRepository: HatenaRepository
) : ViewModel() {

    class Factory(
        private val entryKind: HatenaEntry.Kind,
        private val entryCategory: HatenaEntry.Category,
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    HatenaEntry.Kind::class.java,
                    HatenaEntry.Category::class.java,
                    HatenaRepository::class.java
                )
                .newInstance(
                    entryKind,
                    entryCategory,
                    HatenaRepository.Factory(context).create()
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
                    hatenaRepository.getHotEntryList(entryCategory, pageNumber)
                }
                HatenaEntry.Kind.NEW -> {
                    hatenaRepository.getNewEntryList(entryCategory, pageNumber)
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