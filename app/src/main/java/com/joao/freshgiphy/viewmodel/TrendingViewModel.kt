package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.ui.NetworkState
import com.joao.freshgiphy.ui.datasource.GifDataFactory
import com.joao.freshgiphy.ui.datasource.GifDataSource
import java.util.concurrent.Executors


class TrendingViewModel constructor(
    private val repository: IGiphyRepository,
    dataFactory: GifDataFactory
) : ViewModel() {

    private val executor = Executors.newFixedThreadPool(5)

    private val networkState = Transformations.switchMap<GifDataSource, NetworkState>(dataFactory.mutableLiveData) {
        it.networkState
    }

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setInitialLoadSizeHint(10)
        .setPageSize(25).build()

    private val gifsLiveData = LivePagedListBuilder<Long, Gif>(dataFactory, pagedListConfig)
        .setFetchExecutor(executor)
        .build()

    fun getNetworkState() = networkState

    fun getGifs() = gifsLiveData

}

class TrendingViewModelFactory(
    private val repository: IGiphyRepository,
    private val dataFactory: GifDataFactory
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrendingViewModel(repository, dataFactory) as T
    }
}