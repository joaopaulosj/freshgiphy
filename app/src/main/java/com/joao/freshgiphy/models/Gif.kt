package com.joao.freshgiphy.models

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.joao.freshgiphy.api.responses.GifResponse

@Entity
data class Gif(
    @PrimaryKey val id: String,
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