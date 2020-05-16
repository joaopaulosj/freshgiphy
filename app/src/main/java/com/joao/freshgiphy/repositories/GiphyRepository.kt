package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import io.reactivex.Single

class GiphyRepository constructor(private val service: GiphyService) : IGiphyRepository {

    override fun getTrending(page: Int): Single<ApiResponse> {
        return service.getTrending(page)
    }

    override fun search(query: String, page: Int): Single<ApiResponse> {
        return service.search(query, page)
    }

    override fun getFavourites(): Single<List<GifResponse>> {
        return Single.just(emptyList()) //TODO
    }

}