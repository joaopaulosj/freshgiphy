package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.utils.SingleLiveEvent
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class GiphyRepository constructor(private val service: GiphyService, private val db: GifDatabase) : IGiphyRepository {

    override val favouriteChangeEvent = SingleLiveEvent<Gif>()
    override val trendingChangeEvent = SingleLiveEvent<Gif>()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return doRequest(service.getTrending(offset))
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return doRequest(service.search(query, offset))
    }

    // This method joins the api request with the local favourites
    // to mark them as favourite if applicable, before showing the list
    private fun doRequest(request: Single<ApiResponse>): Single<ApiResponse> {
        return Single.zip(
            request,
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                fromApi.data.forEach { apiGif ->
                    apiGif.isFavourite = fromDb.any { it.id == apiGif.id }
                }

                fromApi
            }
        )
    }

    override fun getFavourites() = db.userDao().getAll()

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

                    favouriteChangeEvent.postValue(gif)
                    trendingChangeEvent.postValue(gif)
                    dispose()
                }

                override fun onError(e: Throwable) {
                    dispose()
                }
            })
    }
}