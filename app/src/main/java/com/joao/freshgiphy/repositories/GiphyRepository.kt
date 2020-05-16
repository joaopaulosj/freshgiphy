package com.joao.freshgiphy.repositories

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class GiphyRepository constructor(
    private val service: GiphyService,
    private val db: GifDatabase
) : IGiphyRepository {

    private val favouritesLiveData = db.userDao().getAll()

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return service.getTrending(offset)
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return service.search(query, offset)
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