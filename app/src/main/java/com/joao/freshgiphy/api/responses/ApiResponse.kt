package com.joao.freshgiphy.api.responses

data class ApiResponse(
    val data: List<GiphyResponse> = emptyList(),
    val pagination: PaginationResponse,
    val meta: MetaResponse
)

data class GiphyResponse(
    val id: String,
    val url: String
)

data class PaginationResponse(
    val total_count: Int,
    val count: Int,
    val offset: Int
)

data class MetaResponse(
    val status: Int,
    val msg: String
)