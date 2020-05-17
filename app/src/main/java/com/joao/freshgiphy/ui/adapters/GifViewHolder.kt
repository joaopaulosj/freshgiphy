package com.joao.freshgiphy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import kotlinx.android.synthetic.main.item_gif.view.*
import kotlin.random.Random

class GifViewHolder(
    view: View,
    private val listener: GifClickListener,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(view) {

    private val colors = listOf(
        R.color.colorGreen,
        R.color.colorCyan,
        R.color.colorTeal,
        R.color.colorBlue,
        R.color.colorDeepPurple,
        R.color.colorYellow,
        R.color.colorAmber
    )

    fun bind(item: Gif) {
        itemView.apply {
            itemTrendingImg.setDimensions(item.height, item.width)

            val colorIndex = Random.nextInt(0, colors.size)

            glide.load(item.url)
                .placeholder(colors[colorIndex])
                .into(itemTrendingImg)

            val favIcon = if (item.isFavourite) R.drawable.ic_star else R.drawable.ic_star_border
            itemTrendingFavImg.setImageResource(favIcon)

            itemTrendingFavImg.setOnClickListener { listener.onFavouriteClicked(item) }
        }
    }
}

interface GifClickListener {
    fun onFavouriteClicked(gif: Gif)
}