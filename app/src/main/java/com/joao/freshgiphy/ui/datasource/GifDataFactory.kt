package com.joao.freshgiphy.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.joao.freshgiphy.models.Gif

class GifDataFactory(private val feedDataSource: GifDataSource) : DataSource.Factory<Long, Gif>() {
    val mutableLiveData = MutableLiveData<GifDataSource>()

    override fun create(): DataSource<Long, Gif> {
        mutableLiveData.postValue(feedDataSource)
        return feedDataSource
    }
}