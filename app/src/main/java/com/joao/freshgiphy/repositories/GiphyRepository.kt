package com.joao.freshgiphy.repositories

import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.api.responses.GiphyResponse
import javax.inject.Inject

class GiphyRepository @Inject constructor(private val service: GiphyService) : IGiphyRepository {

    override fun getTrending(): GiphyResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun search(query: String, page: Int): GiphyResponse {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}