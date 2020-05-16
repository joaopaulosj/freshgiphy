package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GiphyResponse
import io.reactivex.Single

class GiphyRepository constructor(private val service: GiphyService) : IGiphyRepository {

    override fun getTrending(): Single<ApiResponse> {
        return service.getTrending()
    }

    override fun search(query: String, page: Int): Single<ApiResponse> {
        return service.search(query, page)
    }

    override fun getFavourites(): Single<List<GiphyResponse>> {
        return Single.just(emptyList()) //TODO
    }

}