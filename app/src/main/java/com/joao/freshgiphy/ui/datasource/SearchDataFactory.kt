package com.joao.freshgiphy.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.joao.freshgiphy.models.Gif

class SearchDataFactory(private val searchDataSource: TrendingDataSource) : DataSource.Factory<Long, Gif>() {

    val mutableLiveData = MutableLiveData<TrendingDataSource>()

    override fun create(): DataSource<Long, Gif> {
        mutableLiveData.postValue(searchDataSource)
        return searchDataSource
    }
}