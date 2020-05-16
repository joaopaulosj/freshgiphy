package com.joao.freshgiphy.api.responses

data class ApiResponse(
    val data: List<GifResponse> = emptyList(),
    val pagination: PaginationResponse,
    val meta: MetaResponse
)

data class GifResponse(
    val id: String,
    val url: String,
    val source: String,
    val thumbnail: String,
    val title: String,
    val username: String,
    val images: GifImage
)

data class GifImage(
    val original: GifPreview
)

data class GifPreview(
    val url: String,
    val height: Int,
    val width: Int
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