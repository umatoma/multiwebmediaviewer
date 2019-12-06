package io.github.umatoma.multiwebmediaviewer.view.hatenaEntry

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HatenaEntryViewModel: ViewModel() {

    val isOpenFloatingActionButtonLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    fun toggleFloatingActionButton() {
        val isOpen = isOpenFloatingActionButtonLiveData.value?.not()
        isOpenFloatingActionButtonLiveData.postValue(isOpen)
    }

}