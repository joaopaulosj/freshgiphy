package com.joao.freshgiphy.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.viewmodel.TrendingViewModel

class TrendingDataFactory(private val viewModel: TrendingViewModel) : DataSource.Factory<Long, Gif>() {

    var searchQuery = ""
    val mutableLiveData = MutableLiveData<TrendingDataSource>()

    override fun create(): DataSource<Long, Gif> {
        val trendingDataSource = TrendingDataSource(viewModel, searchQuery)
        mutableLiveData.postValue(trendingDataSource)
        return trendingDataSource
    }

}