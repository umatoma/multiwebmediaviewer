package io.github.umatoma.multiwebmediaviewer.view.home.viewModel

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyRepository
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaRepository

class HomeViewModel(
    private val hatenaRepository: HatenaRepository,
    private val feedlyRepository: FeedlyRepository
): ViewModel() {

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(
                    HatenaRepository::class.java,
                    FeedlyRepository::class.java
                )
                .newInstance(
                    HatenaRepository.Factory(context).create(),
                    FeedlyRepository.Factory(context).create()
                )
        }

        fun create(activity: FragmentActivity): HomeViewModel {
            return ViewModelProviders.of(activity, this)
                .get(HomeViewModel::class.java)
        }
    }

    val isSignedInHatenaLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val isSignedInFeedlyLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val feedlyEntryListCategoryLiveData: MutableLiveData<FeedlyCategory> = MutableLiveData()

    fun signOutHatena() {
        hatenaRepository.signOut()
        fetchIsSignedInHatena()
    }

    fun signOutFeedly() {
        feedlyRepository.signOut()
        fetchIsSignedInFeedly()
    }

    fun fetchIsSignedInAny() {
        fetchIsSignedInHatena()
        fetchIsSignedInFeedly()
    }

    fun setFeedlyEntryListCategory(category: FeedlyCategory) {
        feedlyEntryListCategoryLiveData.postValue(category)
    }

    private fun fetchIsSignedInHatena() {
        isSignedInHatenaLiveData.postValue(hatenaRepository.isSignedIn())
    }

    private fun fetchIsSignedInFeedly() {
        isSignedInFeedlyLiveData.postValue(feedlyRepository.isSignedIn())
    }
}