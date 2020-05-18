package com.joao.freshgiphy.ui.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.joao.freshgiphy.models.Gif
import com.joao.freshgiphy.viewmodel.TrendingViewModel

class TrendingDataFactory(private val viewModel: TrendingViewModel) : DataSource.Factory<Long, Gif>() {

    private val mutableLiveData = MutableLiveData<TrendingDataSource>()
    private var query = ""

    fun updateQuery(query: String) {
        this.query = query
    }

    fun dataSourceLiveData(): MutableLiveData<TrendingDataSource> {
        return mutableLiveData
    }

    override fun create(): DataSource<Long, Gif> {
        val trendingDataSource = TrendingDataSource(viewModel, query)
        mutableLiveData.postValue(trendingDataSource)
        return trendingDataSource
    }

}