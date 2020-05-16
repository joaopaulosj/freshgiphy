package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.extensions.singleSubscribe
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.utils.SingleLiveEvent

class TrendingViewModel constructor(
    private val repository: IGiphyRepository
) : ViewModel() {

    val isLoadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<String>()

    private var currentPage = 0

    private val gifsLiveData: MutableLiveData<List<GifResponse>> by lazy {
        MutableLiveData<List<GifResponse>>().also {
            loadTrendingGifs()
        }
    }

    fun getGifs(): LiveData<List<GifResponse>> {
        return gifsLiveData
    }

    private fun loadTrendingGifs() {
        isLoadingEvent.postValue(true)
        repository.getTrending(currentPage).singleSubscribe(
            onSuccess = {
                gifsLiveData.postValue(it.data)
                isLoadingEvent.postValue(false)
            }, onError = {
                errorEvent.postValue(it.message)
                isLoadingEvent.postValue(false)
            }
        )
    }

}

class TrendingViewModelFactory(
    private val repository: IGiphyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrendingViewModel(repository) as T
    }
}