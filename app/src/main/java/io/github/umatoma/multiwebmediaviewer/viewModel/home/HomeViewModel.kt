package io.github.umatoma.multiwebmediaviewer.viewModel.home

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.github.umatoma.multiwebmediaviewer.model.feedly.entity.FeedlyCategory
import io.github.umatoma.multiwebmediaviewer.model.feedly.repository.FeedlyLocalRepository
import io.github.umatoma.multiwebmediaviewer.model.hatena.repository.HatenaLocalRepository

class HomeViewModel(
    private val hatenaLocalRepository: HatenaLocalRepository,
    private val feedlyLocalRepository: FeedlyLocalRepository
): ViewModel() {

    class Factory(
        private val context: Context
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val hatenaLocalRepository = HatenaLocalRepository(context)
            val feedlyLocalRepository = FeedlyLocalRepository(context)

            return modelClass
                .getConstructor(
                    HatenaLocalRepository::class.java,
                    FeedlyLocalRepository::class.java
                )
                .newInstance(
                    hatenaLocalRepository,
                    feedlyLocalRepository
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
        hatenaLocalRepository.signOut()
        fetchIsSignedInHatena()
    }

    fun signOutFeedly() {
        feedlyLocalRepository.signOut()
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
        isSignedInHatenaLiveData.postValue(hatenaLocalRepository.isSignedIn())
    }

    private fun fetchIsSignedInFeedly() {
        isSignedInFeedlyLiveData.postValue(feedlyLocalRepository.isSignedIn())
    }
}