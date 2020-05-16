package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.responses.GiphyResponse

interface IGiphyRepository {
    fun getTrending(): GiphyResponse
    fun search(query: String, page: Int): GiphyResponse
}