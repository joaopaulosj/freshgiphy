package com.joao.freshgiphy.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
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
import io.reactivex.Single
import java.util.concurrent.Executors

class TrendingViewModel constructor(private val repository: IGiphyRepository) : BaseViewModel() {

    private val executor = Executors.newFixedThreadPool(5)

    override val gifChangedEvent = repository.trendingChangeEvent

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

    fun getNetworkState(): LiveData<NetworkState> = networkState

    fun getGifs(): LiveData<PagedList<Gif>> = gifsLiveData

    fun refresh() {
        invalidateDataFactory()
    }

    fun onSearchQuery(query: String) {
        updateQueryOnDataFactory(query)
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

    override fun onGifClick(gif: Gif) = repository.toggleFavourite(gif)

    private fun doRequest(request: Single<ApiResponse>): Single<ApiResponse> {
        return request
            .doOnSuccess {
                when {
                    it.meta.status != 200 -> listStatusEvent.postValue(ListStatus(Status.ERROR, it.meta.msg))
                    it.data.isEmpty() -> listStatusEvent.postValue(ListStatus(Status.EMPTY))
                    else -> listStatusEvent.postValue(ListStatus(Status.SUCCESS))
                }
            }.doOnError {
                listStatusEvent.postValue(ListStatus(Status.ERROR))
                listStatusEvent.postValue(ListStatus(Status.ERROR, it.message))
            }
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