package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.utils.SingleLiveEvent
import com.joao.freshgiphy.utils.extensions.rxSubscribe
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class GiphyRepository constructor(
    private val service: GiphyService,
    private val db: GifDatabase
) : IGiphyRepository {

    override val favouriteChangeEvent = SingleLiveEvent<Gif>()
    override val trendingChangeEvent = SingleLiveEvent<Gif>()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return doRequest(service.getTrending(offset))
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return doRequest(service.search(query, offset))
    }

    private fun doRequest(request: Single<ApiResponse>): Single<ApiResponse> {
        return Single.zip(
            request,
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        )
    }

    private fun setFavourites(fromApi: ApiResponse, fromDb: List<Gif>): ApiResponse {
        fromApi.data.forEach { apiGif ->
            apiGif.isFavourite = fromDb.any { it.id == apiGif.id }
        }

        return fromApi
    }

    override fun getFavourites() = db.userDao().getAll()

    override fun toggleFavourite(gif: Gif) {
        gif.isFavourite = !gif.isFavourite

        Single.just(gif)
            .rxSubscribe(
                observeOnScheduler = Schedulers.io(),
                onSuccess = {
                    if (gif.isFavourite) {
                        db.userDao().insert(gif)
                    } else {
                        db.userDao().delete(gif.id)
                    }

                    favouriteChangeEvent.postValue(gif)
                    trendingChangeEvent.postValue(gif)
                }
            )
    }
}