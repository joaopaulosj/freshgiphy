package com.joao.freshgiphy.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

/*
 * This custom view was created to facilitate changing the item size
 * depending on the height/width received from the response
 */
class GifImageView(context: Context, attributeSet: AttributeSet) : AppCompatImageView(context, attributeSet) {

    private var aspectRatio: Float = 1f

    fun setDimensions(height: Int, width: Int) {
        this.aspectRatio = height / width.toFloat()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val newHeight = (measuredWidth * aspectRatio).toInt()
        setMeasuredDimension(measuredWidth, newHeight)
    }

}