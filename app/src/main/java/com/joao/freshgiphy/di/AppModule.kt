package com.joao.freshgiphy.di

//import android.app.Application
//import com.joao.freshgiphy.BuildConfig
//import com.joao.freshgiphy.api.ApiInterceptor
//import com.joao.freshgiphy.api.GiphyService
//import com.joao.freshgiphy.repositories.GiphyRepository
//import com.joao.freshgiphy.repositories.IGiphyRepository
//import dagger.Module
//import dagger.Provides
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Singleton
//
//@Module
//class AppModule(application: Application) {
//
//    @Provides
//    @Singleton
//    fun provideGiphyService(): GiphyService {
//        val logIntercpetor = HttpLoggingInterceptor().apply {
//            level = HttpLoggingInterceptor.Level.BODY
//        }
//
//        val httpClient = OkHttpClient.Builder()
//            .addInterceptor(ApiInterceptor())
//            .addInterceptor(logIntercpetor)
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(BuildConfig.GIPHY_BASE_URL)
//            .client(httpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(GiphyService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideRepository(service: GiphyService): IGiphyRepository {
//        return GiphyRepository(service)
//    }
//
//}