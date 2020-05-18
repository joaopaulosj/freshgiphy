package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.models.Status
import com.joao.freshgiphy.utils.SingleLiveEvent
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class GiphyRepository constructor(
    private val service: GiphyService,
    private val db: GifDatabase
) : IGiphyRepository {

    private val onGifChanged = SingleLiveEvent<Gif>()
    private val onFavouriteGifChanged = SingleLiveEvent<Gif>()
    private val listStatusEvent = SingleLiveEvent<ListStatus>()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        if (offset == 0) listStatusEvent.postValue(ListStatus(Status.LOADING))
        return doRequest(service.getTrending(offset))
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        if (offset == 0) listStatusEvent.postValue(ListStatus(Status.LOADING))
        return doRequest(service.search(query, offset))
    }

    private fun doRequest(request: Single<ApiResponse>): Single<ApiResponse> {
        return Single.zip(
            request,
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        ).doOnSuccess {
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

    private fun setFavourites(fromApi: ApiResponse, fromDb: List<Gif>): ApiResponse {
        fromApi.data.forEach { apiGif ->
            apiGif.isFavourite = fromDb.any { it.id == apiGif.id }
        }

        return fromApi
    }

    override fun getFavourites() = db.userDao().getAll()

    override fun onTrendingGifChanged() = onGifChanged

    override fun onFavouriteGifChanged() = onFavouriteGifChanged

    override fun listStatusEvent() = listStatusEvent

    override fun toggleFavourite(gif: Gif) {
        gif.isFavourite = !gif.isFavourite

        Single.just(gif)
            .subscribeOn(Schedulers.io())
            .subscribe(object : DisposableSingleObserver<Gif>() {
                override fun onSuccess(gif: Gif) {
                    if (gif.isFavourite) {
                        db.userDao().insert(gif)
                    } else {
                        db.userDao().delete(gif.id)
                    }

                    onFavouriteGifChanged.postValue(gif)
                    onGifChanged.postValue(gif)
                    dispose()
                }

                override fun onError(e: Throwable) {
                    dispose()
                }
            })
    }
}