package com.joao.freshgiphy.di

import android.content.Context
import androidx.room.Room
import com.joao.freshgiphy.BuildConfig
import com.joao.freshgiphy.api.ApiInterceptor
import com.joao.freshgiphy.api.GifDatabase
import com.joao.freshgiphy.api.GiphyService
import com.joao.freshgiphy.repositories.GiphyRepository
import com.joao.freshgiphy.repositories.IGiphyRepository
import com.joao.freshgiphy.ui.datasource.TrendingDataFactory
import com.joao.freshgiphy.viewmodel.FavouritesViewModelFactory
import com.joao.freshgiphy.viewmodel.TrendingViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {

    //ViewModels
    val trendingViewModelFactory by lazy {
        TrendingViewModelFactory(giphyRepository, gifDataFactory)
    }

    val favouritesViewModelFactory by lazy {
        FavouritesViewModelFactory(giphyRepository)
    }

    //Repository
    private val giphyRepository: IGiphyRepository by lazy {
        GiphyRepository(giphyService, localDb)
    }

    //Data Source
    private val gifDataFactory: TrendingDataFactory by lazy { TrendingDataFactory(giphyRepository) }

    //Local
    private val localDb: GifDatabase by lazy {
        Room.databaseBuilder(
            context,
            GifDatabase::class.java, "gif-database"
        ).build()
    }

    //Service
    private val giphyService: GiphyService by lazy {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(ApiInterceptor())
            .addInterceptor(logInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BuildConfig.GIPHY_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GiphyService::class.java)
    }

}