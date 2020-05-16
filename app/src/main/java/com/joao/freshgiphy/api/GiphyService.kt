package com.joao.freshgiphy.api

import com.joao.freshgiphy.api.responses.ApiResponse
import com.joao.freshgiphy.utils.Constants
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyService {

    @GET("trending?rating=R")
    fun getTrending(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = Constants.GIFS_PER_PAGE
    ): Single<ApiResponse>

    @GET("search?lang=en")
    fun search(
        @Query("q") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = Constants.GIFS_PER_PAGE
    ): Single<ApiResponse>

}