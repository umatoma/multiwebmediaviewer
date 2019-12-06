package io.github.umatoma.multiwebmediaviewer.viewModel.hatenaEntry

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaBookmark
import io.github.umatoma.multiwebmediaviewer.model.hatena.entity.HatenaEntry
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaLocalRepository
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HatenaBookmarkListViewModel(
    private val entry: HatenaEntry,
    private val remoteRepository: HatenaRemoteRepository
) : ViewModel() {

    class Factory(
        private val context: Context,
        private val entry: HatenaEntry
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val localRepository = HatenaLocalRepository(context)
            val remoteRepository = HatenaRemoteRepository(localRepository)

            return modelClass
                .getConstructor(HatenaEntry::class.java, HatenaRemoteRepository::class.java)
                .newInstance(entry, remoteRepository)
        }
    }

    val bookmarkListLiveData: MutableLiveData<ArrayList<HatenaBookmark>> = MutableLiveData()

    fun fetchBookmarkList() {
        if (bookmarkListLiveData.value == null) {
            CoroutineScope(Dispatchers.IO).launch {
                val entryWithBookmarkList = remoteRepository.getEntry(entry.url)
                bookmarkListLiveData.postValue(entryWithBookmarkList.bookmarkList)
            }
        } else {
            bookmarkListLiveData.postValue(bookmarkListLiveData.value)
        }
    }
}