package com.joao.freshgiphy.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.joao.freshgiphy.utils.extensions.rxSubscribe
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.toGif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.ui.NetworkState
import com.joao.freshgiphy.utils.extensions.doNothing
import com.joao.freshgiphy.viewmodel.TrendingViewModel

class TrendingDataSource(
    private val viewModel: TrendingViewModel,
    private val querySearch: String
) : PageKeyedDataSource<Long, Gif>() {

    private val initialLoading = MutableLiveData<NetworkState>()
    val networkState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Long>, callback: LoadInitialCallback<Long, Gif>) {
        initialLoading.postValue(NetworkState.LOADING)
        networkState.postValue(NetworkState.LOADING)

        val call = if (querySearch.isBlank()) {
            viewModel.getTrending(0)
        } else {
            viewModel.search(querySearch, 0)
        }

        call.rxSubscribe(
            onSuccess = {
                if (it.meta.status == 200) {
                    val list = it.data.map { gifResponse -> gifResponse.toGif() }

                    if (list.isNotEmpty()) {
                        callback.onResult(list, null, it.pagination.count.toLong())
                    }

                    initialLoading.postValue(NetworkState.LOADED)
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(
                        NetworkState(
                            (NetworkState.Status.FAILED),
                            it.meta.msg
                        )
                    )
                }
            },
            onError = {
                networkState.postValue(
                    NetworkState(
                        (NetworkState.Status.FAILED),
                        it.message
                    )
                )
            }
        )
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Gif>) {
        doNothing()
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Gif>) {
        networkState.postValue(NetworkState.LOADING)

        val call = if (querySearch.isBlank()) {
            viewModel.getTrending(params.key.toInt())
        } else {
            viewModel.search(querySearch, params.key.toInt())
        }

        call.rxSubscribe(
            onSuccess = {
                if (it.meta.status == 200) {
                    val list = it.data.map { gifResponse -> gifResponse.toGif() }
                    var nextKey: Int? = it.pagination.offset + it.pagination.count

                    if (nextKey ?: 0 > it.pagination.total_count) {
                        nextKey = null
                    }

                    if (list.isNotEmpty()) {
                        callback.onResult(list, nextKey?.toLong())
                    }


                    initialLoading.postValue(NetworkState.LOADED)
                    networkState.postValue(NetworkState.LOADED)
                } else {
                    networkState.postValue(
                        NetworkState(
                            (NetworkState.Status.FAILED),
                            it.meta.msg
                        )
                    )
                }
            },
            onError = {
                networkState.postValue(
                    NetworkState(
                        (NetworkState.Status.FAILED),
                        it.message
                    )
                )
            }
        )
    }
}