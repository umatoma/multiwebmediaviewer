package io.github.umatoma.multiwebmediaviewer.view.home.viewModel

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedlyCategoryListViewModel(
    private val feedlyRepository: FeedlyRepository
) : ViewModel() {

    class Factory(private val context: Context) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(FeedlyRepository::class.java)
                .newInstance(FeedlyRepository.Factory(context).create())
        }

        fun create(fragment: Fragment): FeedlyCategoryListViewModel {
            return ViewModelProviders.of(fragment, this)
                .get(FeedlyCategoryListViewModel::class.java)
        }
    }

    val isFetchingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val feedlyCategoryListLiveData: MutableLiveData<List<FeedlyCategory>> = MutableLiveData()

    fun fetchFeedlyCategoryList() {
        CoroutineScope(Dispatchers.IO).launch {
            val categoryList =
                listOf(createCategoryAll()) + feedlyRepository.getCollections()
            isFetchingLiveData.postValue(false)
            feedlyCategoryListLiveData.postValue(categoryList)
        }
        isFetchingLiveData.postValue(true)
    }

    private fun createCategoryAll(): FeedlyCategory {
        val accessToken = feedlyRepository.getAccessToken()
        return FeedlyCategory.fromUserIdAndLabel(
            accessToken.id,
            FeedlyCategory.Label.GLOBAL_ALL
        )
    }
}