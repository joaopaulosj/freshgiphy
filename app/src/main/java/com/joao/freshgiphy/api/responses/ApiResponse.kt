package com.joao.freshgiphy.api.responses

data class ApiResponse(
    val data: List<GifResponse> = emptyList(),
    val pagination: PaginationResponse,
    val meta: MetaResponse
)

data class GifResponse(
    val id: String,
    val url: String,
    val original: String,
    val thumbnail: String,
    val title: String,
    val username: String
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