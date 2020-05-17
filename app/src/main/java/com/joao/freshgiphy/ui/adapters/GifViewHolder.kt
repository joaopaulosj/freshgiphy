package com.joao.freshgiphy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import kotlinx.android.synthetic.main.item_trending.view.*

class GifViewHolder(
    view: View,
    private val listener: GifClickListener,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(view) {

    fun bind(item: Gif) {
        itemView.apply {
            itemTrendingImg.setDimensions(item.height, item.width)

            glide.load(item.url)
                .placeholder(R.color.colorAccent)
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