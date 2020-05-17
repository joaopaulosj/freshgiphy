package com.joao.freshgiphy.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joao.freshgiphy.api.responses.GifResponse

@Entity
data class Gif(
    @PrimaryKey(autoGenerate = true) val dbId: Long? = null,
    val id: String,
    val url: String,
    val height: Int,
    val width: Int,
    var isFavourite: Boolean = false
)

fun GifResponse.toGif(): Gif {
    return Gif(
        id = id,
        url = images.downsized.url,
        height = images.downsized.height,
        width = images.downsized.width,
        isFavourite = isFavourite
    )
}