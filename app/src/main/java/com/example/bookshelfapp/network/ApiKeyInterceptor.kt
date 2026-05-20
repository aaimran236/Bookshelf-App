package com.example.bookshelfapp.network

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val urlWithKey = originalRequest.url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()
        val newRequest = originalRequest.newBuilder()
            .url(urlWithKey)
            .build()
        return chain.proceed(newRequest)
    }
}