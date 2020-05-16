package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GiphyResponse
import io.reactivex.Single

interface IGiphyRepository {
    fun getTrending(): Single<ApiResponse>
    fun search(query: String, page: Int): Single<ApiResponse>
    fun getFavourites(): Single<List<GiphyResponse>>
}