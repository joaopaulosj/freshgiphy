package com.joao.freshgiphy.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.models.Gif
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class GiphyRepository constructor(
    private val service: GiphyService,
    private val db: GifDatabase
) : IGiphyRepository {

    private val favouritesLiveData = db.userDao().getAll()

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

    @SuppressLint("CheckResult")
    override fun addFavorite(gif: Gif) {
        Completable.complete()
            .subscribeOn(Schedulers.io())
            .subscribe { db.userDao().insert(gif) }
    }

    @SuppressLint("CheckResult")
    override fun removeFavorite(id: String) {
        Completable.complete()
            .subscribeOn(Schedulers.io())
            .subscribe { db.userDao().delete(id) }
    }
}