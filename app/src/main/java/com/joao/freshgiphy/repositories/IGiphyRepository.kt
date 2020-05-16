package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.models.Gif
import io.reactivex.Single

interface IGiphyRepository {
    fun getTrending(page: Int): Single<List<Gif>>
    fun search(query: String, page: Int): Single<ApiResponse>
    fun getFavourites(): Single<List<Gif>>
    fun toggleFavorite(gif: GifResponse): Boolean
}