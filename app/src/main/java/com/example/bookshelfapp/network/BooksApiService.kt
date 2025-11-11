package com.example.bookshelfapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


///https://www.googleapis.com/books/v1/volumes?q=jazz+history

private const val BASE_URL =
    "https://www.googleapis.com/"

private val retrofit = Retrofit.Builder()
    ///.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface BookApiService {
    @GET("books/v1/volumes")
    suspend fun getBookIds(@Query("q") searchQuery: String = "jazz history"): BookResponse
}

object BookApi{

    val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}
