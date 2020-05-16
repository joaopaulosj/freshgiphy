package com.joao.freshgiphy.models

import com.joao.freshgiphy.api.responses.GifResponse

data class Gif(
    val id: String,
    val url: String,
    val height: Int,
    val width: Int,
    var isFavourite: Boolean = false
)

fun GifResponse.toGif(): Gif {
    return Gif(
        id = id,
        url = images.original.url,
        height = images.original.height,
        width = images.original.width
    )
}