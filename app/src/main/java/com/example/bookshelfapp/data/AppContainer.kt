package com.example.bookshelfapp.data

import com.example.bookshelfapp.BuildConfig
import com.example.bookshelfapp.network.ApiKeyInterceptor
import com.example.bookshelfapp.network.BookApiService
import okhttp3.OkHttpClient

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val bookRepository: BookRepository
}

class DefaultAppContainer : AppContainer {

    private val baseUrl = "https://www.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor(BuildConfig.BOOKS_API_KEY))
        .build()

    private val retrofit = Retrofit.Builder()
        ///.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .build()

    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    ///`by lazy` ensures that the code inside the {} block is executed only once,
    /// the very first time this property is accessed. the result is stored and reused.
    override val bookRepository: BookRepository by lazy {
        NetworkBookRepository(retrofitService)
    }
}