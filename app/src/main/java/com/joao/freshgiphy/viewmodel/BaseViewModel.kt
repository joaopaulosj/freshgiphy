package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.ViewModel
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.utils.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    abstract val gifChangedEvent: SingleLiveEvent<Gif>

    val listStatusEvent = SingleLiveEvent<ListStatus>()

    abstract fun onGifClick(gif: Gif)

}