package com.example.bookshelfapp.data


import com.example.bookshelfapp.network.BookApiService

interface BookThumbnailRepository {
    suspend fun getBookThumbnails(): List<String>
}

class NetworkBookThumbnailRepository(
    private val bookApiService: BookApiService
) : BookThumbnailRepository {

    override suspend fun getBookThumbnails(): List<String> {
        val searchBookApiResponse = bookApiService.getBookIds()

        val thumbnailUrls: MutableList<String> = mutableListOf()

        searchBookApiResponse.items.forEach {
            try {
                val bookInfo = bookApiService.getBookInfo(it.id)
                bookInfo.volumeInfo?.imageLinks?.thumbnail?.let {
                    stringUrl->
                    val imageUrl = stringUrl.replace("http://", "https://")
                    thumbnailUrls.add(imageUrl)
                }
            } catch (e: Exception) {

            }
        }
        return thumbnailUrls
    }
}
