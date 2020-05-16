package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.toGif
import io.reactivex.Single

class GiphyRepository constructor(private val service: GiphyService) : IGiphyRepository {

    override fun getTrending(page: Int): Single<List<Gif>> {
        return service.getTrending(page).flatMap {
            Single.just(it.data.map { gifResponse ->
                gifResponse.toGif()
            })
        }
    }

    override fun search(query: String, page: Int): Single<ApiResponse> {
        return service.search(query, page)
    }

    override fun getFavourites(): Single<List<Gif>> {
        return Single.just(emptyList()) //TODO
    }

    override fun toggleFavorite(gif: GifResponse): Boolean {
        return false
    }
}