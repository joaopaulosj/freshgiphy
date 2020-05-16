package com.joao.freshgiphy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.NetworkState
import com.joao.freshgiphy.R
import kotlinx.android.synthetic.main.item_trending.view.*


class FavouritesAdapter(val listener: ClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //TODO put glide



    inner class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Gif) {
            itemView.apply {
                itemTrendingImg.setDimensions(item.height, item.width)

                Glide.with(context)
                    .load(item.url)
                    .placeholder(R.color.colorAccent)
                    .into(itemTrendingImg)

                itemTrendingFavImg.setOnClickListener {
                    listener.onFavClicked(item)
                }
            }
        }
    }

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
        }
    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
        }
    }

    interface ClickListener {
        fun onFavClicked(gif: Gif)
    }
}