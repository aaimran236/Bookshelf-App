package com.example.bookshelfapp.data

import com.example.bookshelfapp.network.BookApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val bookThumbnailRepository: BookThumbnailRepository
}

class DefaultAppContainer: AppContainer{
    private val BASE_URL =
        "https://www.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        ///.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    override val bookThumbnailRepository: BookThumbnailRepository by lazy{
        NetworkBookThumbnailRepository(retrofitService)
    }
}