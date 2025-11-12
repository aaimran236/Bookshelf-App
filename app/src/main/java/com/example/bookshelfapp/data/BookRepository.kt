package com.example.bookshelfapp.data

import com.example.bookshelfapp.network.BookApi

interface BookThumbnailRepository {
    suspend fun getBookThumbnails(): List<String>
}

class NetworkBookThumbnailRepository : BookThumbnailRepository {

    override suspend fun getBookThumbnails(): List<String> {
        val searchBookApiResponse = BookApi.retrofitService.getBookIds()

        val thumbnailUrls: MutableList<String> = mutableListOf()

        searchBookApiResponse.items.forEach {
            try {
                val bookInfo = BookApi.retrofitService.getBookInfo(it.id)
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
