package com.example.bookshelfapp.data

import com.example.bookshelfapp.network.BookApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val bookThumbnailRepository: BookThumbnailRepository
}

class DefaultAppContainer: AppContainer{
    private val baseUrl =
        "https://www.googleapis.com/"

    private val retrofit = Retrofit.Builder()
        ///.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
    ///`by lazy` ensures that the code inside the {} block is executed only once,
    // the very first time this property is accessed. the result is stored and reused.
    private val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }

    override val bookThumbnailRepository: BookThumbnailRepository by lazy{
        NetworkBookThumbnailRepository(retrofitService)
    }
}