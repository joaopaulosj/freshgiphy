package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.toGif
import io.reactivex.Single

class GiphyRepository constructor(private val service: GiphyService) : IGiphyRepository {

    override fun getTrending(offset: Int): Single<ApiResponse> {
        return service.getTrending(offset)
    }

    override fun search(query: String, offset: Int): Single<ApiResponse> {
        return service.search(query, offset)
    }

    override fun getFavourites(): Single<List<Gif>> {
        return Single.just(emptyList()) //TODO
    }

    override fun toggleFavorite(gif: GifResponse): Boolean {
        return false
    }
}