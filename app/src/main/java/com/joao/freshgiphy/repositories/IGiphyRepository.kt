package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import io.reactivex.Single

interface IGiphyRepository {
    fun getTrending(page: Int): Single<ApiResponse>
    fun search(query: String, page: Int): Single<ApiResponse>
    fun getFavourites(): Single<List<GifResponse>>
}