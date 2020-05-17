package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.ui.NetworkState
import com.joao.freshgiphy.ui.datasource.TrendingDataFactory
import com.joao.freshgiphy.ui.datasource.TrendingDataSource
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.SingleLiveEvent
import java.util.concurrent.Executors


class TrendingViewModel constructor(
    private val repository: IGiphyRepository,
    private val trendingFactory: TrendingDataFactory
) : ViewModel() {

    private val executor = Executors.newFixedThreadPool(5)

    private val networkState =
        Transformations.switchMap<TrendingDataSource, NetworkState>(trendingFactory.mutableLiveData) {
            it.networkState
        }

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(Constants.GIFS_PER_PAGE)
        .build()

    private var gifsLiveData = LivePagedListBuilder<Long, Gif>(trendingFactory, pagedListConfig)
        .setFetchExecutor(executor)
        .build()

    fun getNetworkState(): LiveData<NetworkState> = networkState

    fun getGifs(): LiveData<PagedList<Gif>> = gifsLiveData

    fun onGifChanged(): SingleLiveEvent<Gif> = repository.onGifChanged()

    fun onFavouriteClick(gif: Gif) = repository.toggleFavourite(gif)

    fun refresh() {
        trendingFactory.mutableLiveData.value?.invalidate()
    }

    fun search(query: String) {
        trendingFactory.apply {
            searchQuery = query
            mutableLiveData.value?.invalidate()
        }
    }

}

class TrendingViewModelFactory(
    private val repository: IGiphyRepository,
    private val trendingDataFactory: TrendingDataFactory
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrendingViewModel(repository, trendingDataFactory) as T
    }
}