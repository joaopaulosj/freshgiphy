package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joao.freshgiphy.api.responses.GiphyResponse
import com.joao.freshgiphy.extensions.singleSubscribe
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.utils.SingleLiveEvent

class TrendingViewModel constructor(
    private val repository: IGiphyRepository
) : ViewModel() {

    val errorLiveData = SingleLiveEvent<String>()

    private val gifsLiveData: MutableLiveData<List<GiphyResponse>> by lazy {
        MutableLiveData<List<GiphyResponse>>().also {
            loadTrendingGifs()
        }
    }

    fun getGifs(): LiveData<List<GiphyResponse>> {
        return gifsLiveData
    }

    private fun loadTrendingGifs() {
        repository.getTrending().singleSubscribe(
            onSuccess = {
                gifsLiveData.postValue(it.data)
            }, onError = {
                errorLiveData.postValue(it.message)
            }
        )
    }

}