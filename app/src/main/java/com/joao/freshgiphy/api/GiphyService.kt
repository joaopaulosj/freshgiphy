package com.joao.freshgiphy.api

import com.joao.freshgiphy.api.responses.ApiResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyService {

    @GET("trending")
    fun getTrending(@Query("limit") limit: Int = 25): Single<ApiResponse>

    @GET("search?lang=en")
    fun search(@Query("q") query: String, @Query("limit") limit: Int = 25): Single<ApiResponse>

}