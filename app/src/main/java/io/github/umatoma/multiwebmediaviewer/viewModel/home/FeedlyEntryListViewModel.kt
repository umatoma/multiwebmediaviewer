package io.github.umatoma.multiwebmediaviewer.viewModel.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyEntry
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyStream
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyLocalRepository
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedlyEntryListViewModel(
    private val feedlyLocalRepository: FeedlyLocalRepository,
    private val feedlyRemoteRepository: FeedlyRemoteRepository
) : ViewModel() {

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val feedlyLocalRepository = FeedlyLocalRepository(context)
            val feedlyRemoteRepository = FeedlyRemoteRepository(feedlyLocalRepository)

            return modelClass
                .getConstructor(
                    FeedlyLocalRepository::class.java,
                    FeedlyRemoteRepository::class.java
                )
                .newInstance(
                    feedlyLocalRepository,
                    feedlyRemoteRepository
                )
        }

        fun create(fragment: Fragment): FeedlyEntryListViewModel {
            return ViewModelProviders.of(fragment, this)
                .get(FeedlyEntryListViewModel::class.java)
        }
    }

    val isFetchingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val feedlyCategoryLiveData: MutableLiveData<FeedlyCategory> by lazy {
        MutableLiveData(createCategoryAll())
    }
    val feedlyEntryListLiveData: MutableLiveData<List<FeedlyEntry>> = MutableLiveData()

    private var currentStream: FeedlyStream? = null

    fun fetchFeedlyEntryList(prevStream: FeedlyStream? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val category = feedlyCategoryLiveData.value!!
            val stream = feedlyRemoteRepository.getStreamContents(category, prevStream)
            val entryList = stream.items

            isFetchingLiveData.postValue(false)
            currentStream = stream

            if (prevStream == null) {
                feedlyEntryListLiveData.postValue(entryList)
            } else {
                feedlyEntryListLiveData.postValue(feedlyEntryListLiveData.value!! + entryList)
            }
        }
        isFetchingLiveData.postValue(true)
    }

    fun fetchFeedlyEntryListOnNextPage() {
        fetchFeedlyEntryList(currentStream)
    }

    fun setCategory(category: FeedlyCategory) {
        feedlyCategoryLiveData.postValue(category)
        feedlyEntryListLiveData.postValue(listOf())
        fetchFeedlyEntryList()
    }

    private fun createCategoryAll(): FeedlyCategory {
        val accessToken = feedlyLocalRepository.getAccessToken()
        return FeedlyCategory.fromUserIdAndLabel(
            accessToken.id,
            FeedlyCategory.Label.GLOBAL_ALL
        )
    }
}