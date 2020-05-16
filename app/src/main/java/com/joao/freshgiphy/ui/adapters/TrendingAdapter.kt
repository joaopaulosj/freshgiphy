package com.joao.freshgiphy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joao.freshgiphy.R
import com.joao.freshgiphy.api.responses.GifResponse
import com.joao.freshgiphy.models.Gif
import kotlinx.android.synthetic.main.item_trending.view.*

class TrendingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val gifList = mutableListOf<Gif>()

    fun addItems(items: List<Gif>) {
        gifList.addAll(items)
        notifyDataSetChanged() //TODO
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
                    .placeholder(R.color.colorAccent)
                    .into(itemTrendingImg)
            }
        }
    }
}