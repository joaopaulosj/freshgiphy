package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.utils.SingleLiveEvent
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class GiphyRepository constructor(
    private val service: GiphyService,
    private val db: GifDatabase
) : IGiphyRepository {

    private val onGifChanged = SingleLiveEvent<Gif>()
    private val onFavouriteGifChanged = SingleLiveEvent<Gif>()
    private val emptyListEvent = SingleLiveEvent<Boolean>()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return Single.zip(
            service.getTrending(offset),
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        )
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return Single.zip(
            service.search(query, offset),
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        )
    }

    private fun setFavourites(fromApi: ApiResponse, fromDb: List<Gif>): ApiResponse {
        emptyListEvent.postValue(fromApi.data.isEmpty())

        fromApi.data.forEach { apiGif ->
            apiGif.isFavourite = fromDb.any { it.id == apiGif.id }
        }

        return fromApi
    }

    override fun getFavourites(): Single<List<Gif>> {
        return db.userDao().getAll()
    }

    override fun onGifChanged(): SingleLiveEvent<Gif> {
        return onGifChanged
    }

    override fun onFavouriteGifChanged(): SingleLiveEvent<Gif> {
        return onFavouriteGifChanged
    }

    override fun emptyListEvent(): SingleLiveEvent<Boolean> {
        return emptyListEvent
    }

    //TODO warning
    override fun toggleFavourite(gif: Gif) {
        gif.isFavourite = !gif.isFavourite

        Completable.complete()
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (gif.isFavourite) {
                    db.userDao().insert(gif)
                } else {
                    db.userDao().delete(gif.id)
                }

                onFavouriteGifChanged.postValue(gif)
                onGifChanged.postValue(gif)
            }
    }
}