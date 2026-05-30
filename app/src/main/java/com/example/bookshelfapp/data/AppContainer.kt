package com.example.bookshelfapp.data

import com.example.bookshelfapp.BuildConfig
import com.example.bookshelfapp.network.ApiKeyInterceptor
import com.example.bookshelfapp.network.BookApiService
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Application-level dependency container interface.
 * Exposes only the dependencies that the rest of the app needs.
 */
interface AppContainer {
    val bookRepository: BookRepository
}

/**
 * Production implementation of [AppContainer].
 *
 * Every property uses `by lazy` so it is created only on first access.
 *
 * Dependencies that depend on other dependencies are declared AFTER those
 * dependencies to keep the construction order clear and predictable.
 */
class DefaultAppContainer : AppContainer {

    private val baseUrl = "https://www.googleapis.com/"

    /**
     * Adds the API key as a query parameter to every outgoing request.
     * This means no individual service method needs to carry a key parameter.
     */
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(BuildConfig.BOOKS_API_KEY))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private val bookApiService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    override val bookRepository: BookRepository by lazy {
        NetworkBookRepository(bookApiService)
    }
}