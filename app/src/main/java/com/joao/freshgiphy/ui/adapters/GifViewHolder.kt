package com.joao.freshgiphy.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import kotlinx.android.synthetic.main.item_trending.view.*

class GifViewHolder(
    view: View, private val listener: GifClickListener
) : RecyclerView.ViewHolder(view) {

    fun bind(item: Gif) {
        itemView.apply {
            itemTrendingImg.setDimensions(item.height, item.width)

            Glide.with(context)
                .load(item.url)
                .placeholder(R.color.colorAccent)
                .into(itemTrendingImg)

            val favIcon = if (item.isFavourite) R.drawable.ic_star else R.drawable.ic_star_border
            itemTrendingFavImg.setImageResource(favIcon)

            itemTrendingFavImg.setOnClickListener { listener.onFavClicked(item) }
        }
    }
}

interface GifClickListener {
    fun onFavClicked(gif: Gif)
}