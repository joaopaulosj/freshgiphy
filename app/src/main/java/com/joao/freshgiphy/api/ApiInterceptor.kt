package com.joao.freshgiphy.api

import com.joao.freshgiphy.BuildConfig
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class ApiInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl: HttpUrl = originalRequest.url()

        val url = originalUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.GIPHY_API_KEY)
            .build()

        val request = originalRequest.newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }

}