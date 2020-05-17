package com.joao.freshgiphy.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
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

    private val favouritesLiveData = db.userDao().getAll()
    private val onGifChanged = SingleLiveEvent<Gif>()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return Single.zip(
            service.getTrending(offset),
            db.userDao().getAllRx(),
            BiFunction<ApiResponse, List<Gif>, ApiResponse> { fromApi, fromDb ->
                return@BiFunction setFavourites(fromApi, fromDb)
            }
        )
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return Single.zip(
            service.search(query, offset),
            db.userDao().getAllRx(),
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

    override fun getFavourites(): LiveData<List<Gif>> {
        return favouritesLiveData
    }

    override fun onGifChanged(): SingleLiveEvent<Gif> {
        return onGifChanged
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

                onGifChanged.postValue(gif)
            }
    }
}