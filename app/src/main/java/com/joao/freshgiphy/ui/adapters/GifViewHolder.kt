package com.joao.freshgiphy.ui.adapters

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.joao.freshgiphy.R
import com.joao.freshgiphy.models.Gif
import kotlinx.android.synthetic.main.item_gif.view.*
import kotlin.random.Random

class GifViewHolder(
    view: View,
    private val listener: GifClickListener,
    private val glide: RequestManager
) : RecyclerView.ViewHolder(view) {

    private val colors = listOf(
        R.color.colorGreen,
        R.color.colorCyan,
        R.color.colorTeal,
        R.color.colorBlue,
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
                override fun onAnimationRepeat(animation: Animator?) {
                    // Do Nothing
                }

                override fun onAnimationEnd(animation: Animator?) {
                    itemGifFavAnim.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    // Do Nothing
                }

                override fun onAnimationStart(animation: Animator?) {
                    // Do Nothing
                }
            })

            itemGifFavImg.progress = if (item.isFavourite) 1f else 0f
            itemGifFavImg.setOnClickListener {
                if (!item.isFavourite) {
                    itemGifFavAnim.visibility = View.VISIBLE
                    itemGifFavAnim.playAnimation()
                }

                listener.onFavouriteClicked(item)
            }
        }
    }
}

interface GifClickListener {
    fun onFavouriteClicked(gif: Gif)
}