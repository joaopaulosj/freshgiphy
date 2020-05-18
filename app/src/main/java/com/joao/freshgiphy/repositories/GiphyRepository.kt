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

class GiphyRepository constructor(
    private val service: GiphyService,
    private val db: GifDatabase
) : IGiphyRepository {

    private val onGifChanged = SingleLiveEvent<Gif>()
    private val onFavouriteGifChanged = SingleLiveEvent<Gif>()
    private val emptyListEvent = SingleLiveEvent<Boolean>()
    private val errorEvent = SingleLiveEvent<String>()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return Single.zip(
            service.getTrending(offset),
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        ).doOnSuccess {
            if (it.meta.status != 200) {
                errorEvent.postValue(it.meta.msg)
            }
        }.doOnError {
            errorEvent.postValue(it.message)
        }
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return Single.zip(
            service.search(query, offset),
            db.userDao().getAll(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        ).doOnSuccess {
            if (it.meta.status != 200) {
                errorEvent.postValue(it.meta.msg)
            }
        }.doOnError {
            errorEvent.postValue(it.message)
        }
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

    override fun onErrorReceived(): SingleLiveEvent<String> {
        return errorEvent
    }

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