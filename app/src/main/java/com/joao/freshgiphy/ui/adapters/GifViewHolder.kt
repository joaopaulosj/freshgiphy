package com.joao.freshgiphy.ui.adapters

import android.animation.Animator
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.utils.extensions.doNothing
import kotlinx.android.synthetic.main.item_gif.view.*
import kotlin.random.Random

class GifViewHolder(
    view: View,
    private val listener: GifClickListener,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(view) {

    private val colors = listOf(
        R.color.colorGreenLight,
        R.color.colorCyan,
        R.color.colorTeal,
        R.color.colorDeepPurple,
        R.color.colorYellow,
        R.color.colorPink
    )

    fun bind(item: Gif) {
        itemView.apply {
            itemGifImg.setDimensions(item.height, item.width)

            val colorIndex = Random.nextInt(0, colors.size)

            glide.load(item.url)
                .placeholder(colors[colorIndex])
                .into(itemGifImg)

            itemGifFavAnim.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) = doNothing()

                override fun onAnimationCancel(animation: Animator?) = doNothing()

                override fun onAnimationStart(animation: Animator?) = doNothing()

                override fun onAnimationEnd(animation: Animator?) {
                    itemGifFavAnim.visibility = View.GONE
                }
            })

            itemGifFavImg.progress = if (item.isFavourite) 1f else 0f
            itemGifFavImg.setOnClickListener {
                if (!item.isFavourite) {
                    itemGifFavAnim.visibility = View.VISIBLE
                    itemGifFavAnim.playAnimation()
                }

                listener.onGifClick(item)
            }
        }
    }
}

interface GifClickListener {
    fun onGifClick(gif: Gif)
}