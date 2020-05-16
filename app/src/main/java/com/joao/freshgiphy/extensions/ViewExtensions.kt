package com.joao.freshgiphy.extensions

import android.content.res.Resources
import android.util.TypedValue

fun Int.asDp() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
).toInt()


fun Float.asDp() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this,
    Resources.getSystem().displayMetrics
)