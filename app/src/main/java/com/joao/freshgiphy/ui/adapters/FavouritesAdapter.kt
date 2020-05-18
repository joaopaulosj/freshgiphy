package com.joao.freshgiphy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.R


class FavouritesAdapter(
    private val listener: GifClickListener,
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var favouriteGifs = mutableListOf<Gif>()

    fun setItems(items: List<Gif>) {
        favouriteGifs.clear()
        favouriteGifs.addAll(items)
        notifyDataSetChanged()
    }

    fun updateItem(item: Gif) {
        if (item.isFavourite) {
            favouriteGifs.add(0, item)
            notifyItemInserted(0)
        } else {
            val position = favouriteGifs.indexOfFirst { it.id == item.id }

            if (position == -1) return

            favouriteGifs.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gif, parent, false)
        return GifViewHolder(view, listener, glide)
    }

    override fun getItemCount() = favouriteGifs.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GifViewHolder) {
            holder.bind(favouriteGifs[position])
        }
    }
}