package com.example.bookshelfapp.network


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//private const val BASE_URL =
//    "https://www.googleapis.com/"
//
//private val retrofit = Retrofit.Builder()
//    ///.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
//    .addConverterFactory(GsonConverterFactory.create())
//    .baseUrl(BASE_URL)
//    .build()

interface BookApiService {
    ///search results for the term "jazz history"
    ///URL: https://www.googleapis.com/books/v1/volumes?q=jazz+history

    @GET("books/v1/volumes")
    suspend fun getBookIds(
        @Query("q") searchQuery: String = "jazz history"
    ): SearchBooksResponse

    ///request to get info on a specific book
    ///URL: https://www.googleapis.com/books/v1/volumes/<volume_id>

    @GET("books/v1/volumes/{volumeId}")
    suspend fun getBookInfo(@Path("volumeId") volumeId: String): SpecificBookResponse

}

//object BookApi {
//    val retrofitService: BookApiService by lazy {
//        retrofit.create(BookApiService::class.java)
//    }
//}
