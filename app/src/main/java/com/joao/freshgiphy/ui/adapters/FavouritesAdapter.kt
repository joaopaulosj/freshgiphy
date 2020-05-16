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


class FavouritesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var gifList = listOf<Gif>()

    fun setItems(items: List<Gif>) {
        gifList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trending, parent, false)
        return GifViewHolder(view)
    }

    override fun getItemCount() = gifList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GifViewHolder) {
            holder.bind(gifList[position])
        }
    }

    private class GifViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Gif) {
            itemView.apply {
                itemTrendingImg.setDimensions(item.height, item.width)

                Glide.with(context)
                    .load(item.url)
                    .placeholder(R.color.colorPrimary)
                    .into(itemTrendingImg)
            }
        }
    }
}