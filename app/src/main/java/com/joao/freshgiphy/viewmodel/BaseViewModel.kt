package com.joao.freshgiphy.viewmodel

import androidx.lifecycle.ViewModel
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.models.ListStatus
import com.joao.freshgiphy.utils.SingleLiveEvent

abstract class BaseViewModel : ViewModel() {

    protected val listStatusEvent = SingleLiveEvent<ListStatus>()
    protected abstract val gifChangedEvent: SingleLiveEvent<Gif>

    fun listStatusEvent(): SingleLiveEvent<ListStatus> = listStatusEvent
    fun gifChangedEvent(): SingleLiveEvent<Gif> = gifChangedEvent

}