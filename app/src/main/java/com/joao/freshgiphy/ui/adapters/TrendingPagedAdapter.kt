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


class TrendingPagedAdapter(
    val listener: GifClickListener
) : PagedListAdapter<Gif, RecyclerView.ViewHolder>(diffCallback) {
    //TODO put glide

    companion object {
        private const val TYPE_LOADING = 0
        private const val TYPE_GIF = 1

        private val diffCallback = object : DiffUtil.ItemCallback<Gif>() {
            override fun areItemsTheSame(oldItem: Gif, newItem: Gif): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Gif, newItem: Gif): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var networkState = NetworkState.LOADING

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val gif = getItem(position)

        if (gif == null) {
            (holder as EmptyViewHolder).bind()
            return
        }

        when (holder) {
            is GifViewHolder -> holder.bind(gif)
            is LoadingViewHolder -> holder.bind()
            else -> (holder as EmptyViewHolder).bind()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LOADING -> {
                LoadingViewHolder(parent)
            }
            TYPE_GIF -> {
                val view = layoutInflater.inflate(R.layout.item_trending, parent, false)
                GifViewHolder(view, listener)
            }
            else -> {
                EmptyViewHolder(parent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            TYPE_LOADING
        } else {
            TYPE_GIF
        }
    }

    private fun hasExtraRow(): Boolean {
        return networkState !== NetworkState.LOADED
    }

    fun setNetworkState(newNetworkState: NetworkState) {
        val previousState = networkState
        val previousExtraRow = hasExtraRow()

        networkState = newNetworkState
        val newExtraRow = hasExtraRow()

        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newNetworkState) {
            notifyItemChanged(itemCount - 1)
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
}

interface GifClickListener {
    fun onFavClicked(gif: Gif)
}

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

            itemTrendingFavImg.setImageResource(getFavIcon(item))

            itemTrendingFavImg.setOnClickListener {
                item.isFavourite = !item.isFavourite
                itemTrendingFavImg.setImageResource(getFavIcon(item))
                listener.onFavClicked(item)
            }
        }
    }

    private fun getFavIcon(item: Gif) = if (item.isFavourite) R.drawable.ic_star else R.drawable.ic_star_border
}