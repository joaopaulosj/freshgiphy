package com.joao.freshgiphy.di

import com.joao.freshgiphy.BuildConfig
import com.joao.freshgiphy.api.ApiInterceptor
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.repositories.GiphyRepository
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.ui.datasource.GifDataFactory
import com.joao.freshgiphy.ui.datasource.GifDataSource
import com.joao.freshgiphy.viewmodel.FavouritesViewModelFactory
import com.joao.freshgiphy.viewmodel.TrendingViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object AppInjector {

    //ViewModels
    fun getTrendingViewModelFactory() = TrendingViewModelFactory(getGiphyRepository(), getGifDataFactory())

    fun getFavouritesViewModelFactory() = FavouritesViewModelFactory(getGiphyRepository())

    //Repository
    private fun getGiphyRepository(): IGiphyRepository = GiphyRepository(getGiphyService())

    //Data Source
    private fun getGifDataFactory(): GifDataFactory = GifDataFactory(getGifDatasource())
    private fun getGifDatasource(): GifDataSource = GifDataSource(getGiphyRepository())

    //Service
    private fun getGiphyService(): GiphyService {
        val logIntercpetor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor())
            .addInterceptor(logIntercpetor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.GIPHY_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GiphyService::class.java)
    }

}