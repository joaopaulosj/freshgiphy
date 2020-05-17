package com.joao.freshgiphy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.R


class FavouritesAdapter(
    private val listener: GifClickListener,
    private val emptyListener: EmptyListListener,
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var gifList = mutableListOf<Gif>()

    fun setItems(items: List<Gif>) {
        gifList.clear()
        gifList.addAll(items)
        notifyDataSetChanged()

        emptyListener.isListEmpty(gifList.isEmpty())
    }

    fun updateItem(item: Gif) {
        if (item.isFavourite) {
            gifList.add(0, item)
            notifyItemInserted(0)
        } else {
            val position = gifList.indexOfFirst { it.id == item.id }

            if (position == -1) return

            gifList.removeAt(position)
            notifyItemRemoved(position)
        }

        emptyListener.isListEmpty(gifList.isEmpty())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
        return GifViewHolder(view, listener, glide)
    }

    override fun getItemCount() = gifList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GifViewHolder) {
            holder.bind(gifList[position])
        }
    }

    interface EmptyListListener {
        fun isListEmpty(isEmpty: Boolean)
    }
}