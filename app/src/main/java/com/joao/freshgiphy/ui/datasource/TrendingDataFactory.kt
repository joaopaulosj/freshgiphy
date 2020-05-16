package com.joao.freshgiphy.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository

class TrendingDataFactory(private val repository: IGiphyRepository) : DataSource.Factory<Long, Gif>() {

    var searchQuery = ""
    val mutableLiveData = MutableLiveData<TrendingDataSource>()

    override fun create(): DataSource<Long, Gif> {
        val trendingDataSource = TrendingDataSource(repository, searchQuery)
        mutableLiveData.postValue(trendingDataSource)
        return trendingDataSource
    }

}