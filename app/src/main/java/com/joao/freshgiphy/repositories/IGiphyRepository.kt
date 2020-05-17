package com.joao.freshgiphy.repositories

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.utils.SingleLiveEvent
import io.reactivex.Single

interface IGiphyRepository {
    fun getTrending(offset: Int): Single<ApiResponse>
    fun search(query: String, offset: Int): Single<ApiResponse>
    fun getFavourites(): LiveData<List<Gif>>
    fun addFavourite(gif: Gif)
    fun removeFavourite(gif: Gif)
    fun onGifChanged(): SingleLiveEvent<Gif>
}