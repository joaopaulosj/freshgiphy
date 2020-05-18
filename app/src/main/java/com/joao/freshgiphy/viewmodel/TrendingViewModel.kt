package com.joao.freshgiphy.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.ui.NetworkState
import com.joao.freshgiphy.ui.datasource.TrendingDataFactory
import com.joao.freshgiphy.ui.datasource.TrendingDataSource
import com.joao.freshgiphy.utils.Constants
import com.joao.freshgiphy.utils.SingleLiveEvent
import com.joao.freshgiphy.utils.extensions.rxSubscribe
import io.reactivex.Single
import java.util.concurrent.Executors

class TrendingViewModel constructor(
    private val repository: IGiphyRepository
) : ViewModel() {

    private val executor = Executors.newFixedThreadPool(5)
    private val listStatusEvent = SingleLiveEvent<ListStatus>()
    private val onGifChangedLiveData = MediatorLiveData<Gif>()

    @VisibleForTesting
    var trendingDataFactory = TrendingDataFactory(this)

    private val networkState = Transformations.switchMap<TrendingDataSource, NetworkState>(
        TrendingDataFactory(this).mutableLiveData
    ) {
        it.networkState
    }

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(Constants.GIFS_PER_PAGE)
        .build()

    private var gifsLiveData = LivePagedListBuilder<Long, Gif>(trendingDataFactory, pagedListConfig)
        .setFetchExecutor(executor)
        .build()

    init {
        onGifChangedLiveData.addSource(repository.trendingChangeEvent) {
            onGifChangedLiveData.postValue(it)
        }
    }

    fun getNetworkState(): LiveData<NetworkState> = networkState

    fun getGifs(): LiveData<PagedList<Gif>> = gifsLiveData

    fun onGifChanged(): MediatorLiveData<Gif> = onGifChangedLiveData

    fun onFavouriteClick(gif: Gif) {
        repository.toggleFavourite(gif)
    }

    fun listStatusEvent(): SingleLiveEvent<ListStatus> = listStatusEvent

    fun refresh() {
        invalidateDataFactory()
    }

    fun search(query: String, offset: Int): Single<ApiResponse> {
        if (offset == 0) listStatusEvent.postValue(ListStatus(Status.LOADING))
        return doRequest(repository.search(query, offset))
    }

    fun getTrending(offset: Int): Single<ApiResponse> {
        if (offset == 0) listStatusEvent.postValue(ListStatus(Status.LOADING))
        return doRequest(repository.getTrending(offset))
    }

    private fun doRequest(request: Single<ApiResponse>): Single<ApiResponse> {
        return request
            .doOnSuccess {
                when {
                    it.meta.status != 200 -> listStatusEvent.postValue(ListStatus(Status.ERROR, it.meta.msg))
                    it.data.isEmpty() -> listStatusEvent.postValue(ListStatus(Status.EMPTY))
                    else -> listStatusEvent.postValue(ListStatus(Status.DEFAULT))
                }
            }.doOnError {
                listStatusEvent.postValue(ListStatus(Status.ERROR))
                listStatusEvent.postValue(ListStatus(Status.ERROR, it.message))
            }
    }

    fun search(query: String) {
        updateQueryOnDataFactory(query)
        invalidateDataFactory()
    }

    @VisibleForTesting
    fun updateQueryOnDataFactory(query: String) {
        trendingDataFactory.searchQuery = query
    }

    @VisibleForTesting
    fun invalidateDataFactory() {
        trendingDataFactory.mutableLiveData.value?.invalidate()
    }

}

class TrendingViewModelFactory(
    private val repository: IGiphyRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrendingViewModel(repository) as T
    }
}