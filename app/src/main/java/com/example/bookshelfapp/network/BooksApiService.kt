package com.example.bookshelfapp.network


import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*private const val BASE_URL =
    "https://www.googleapis.com/"

private val retrofit = Retrofit.Builder()
    ///.addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()*/

interface BookApiService {

    /**
     * Searches for books matching [searchQuery].
     * [startIndex] and [maxResults] control which page of results to return.
     *
     * https://www.googleapis.com/books/v1/volumes?q=jazz+history&key=myapikey for searchQuery= jazz history
     */

    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") searchQuery: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int,
    ): SearchBooksResponse


    /**
     * Fetches the full details for a single book identified by [volumeId].
     * URL: https://www.googleapis.com/books/v1/volumes/<volume_id>
     */
    @GET("books/v1/volumes/{volumeId}")
    suspend fun getBookDetails(
        @Path("volumeId") volumeId: String,
    ): SpecificBookResponse
}

/*object BookApi {
    val retrofitService: BookApiService by lazy {
        retrofit.create(BookApiService::class.java)
    }
}*/
