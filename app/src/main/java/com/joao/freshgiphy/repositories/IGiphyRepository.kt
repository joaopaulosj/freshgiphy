package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.utils.SingleLiveEvent
import io.reactivex.Single

interface IGiphyRepository {
    fun search(query: String, offset: Int): Single<ApiResponse>
    fun getTrending(offset: Int): Single<ApiResponse>
    fun getFavourites(): Single<List<Gif>>
    fun toggleFavourite(gif: Gif)
    fun onTrendingGifChanged(): SingleLiveEvent<Gif>
    fun onFavouriteGifChanged(): SingleLiveEvent<Gif>
    fun listStatusEvent(): SingleLiveEvent<ListStatus>
}