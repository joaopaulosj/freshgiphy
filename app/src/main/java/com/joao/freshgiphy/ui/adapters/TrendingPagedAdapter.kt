package com.joao.freshgiphy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.ui.NetworkState
import com.joao.freshgiphy.R


class TrendingPagedAdapter(
    private val listener: GifClickListener,
    private val glide: RequestManager
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
        val gif = getItem(position) ?: return

        if (holder is GifViewHolder) {
            holder.bind(gif)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LOADING -> {
                val view = layoutInflater.inflate(R.layout.item_loading, parent, false)
                LoadingViewHolder(view)
            }
            TYPE_GIF -> {
                val view = layoutInflater.inflate(R.layout.item_trending, parent, false)
                GifViewHolder(view, listener, glide)
            }
            else -> EmptyViewHolder(parent)
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

    fun updateItem(item: Gif) {
        val index = currentList?.indexOfFirst { it.id == item.id } ?: -1

        if (index >= 0) {
            getItem(index)?.isFavourite = item.isFavourite
            notifyItemChanged(index)
        }
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

    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}