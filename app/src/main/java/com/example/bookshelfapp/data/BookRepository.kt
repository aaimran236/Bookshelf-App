package com.example.bookshelfapp.data

import com.example.bookshelfapp.network.BookApi

interface BookThumbnailRepository {
    suspend fun getBookThumbnails(): List<String>
}

class NetworkBookThumbnailRepository : BookThumbnailRepository{

    override suspend fun getBookThumbnails(): List<String> {
        val bookApiResponse= BookApi.retrofitService.getBookIds()
        val bookIds=bookApiResponse.items.map {
            it.id
        }

        val thumbnailUrls: MutableList<String> =mutableListOf()

        bookIds.forEach {
            id->
            try {
                val bookInfo= BookApi.retrofitService.getBookInfo(id)

                bookInfo.volumeInfo?.imageLinks?.thumbnail?.let {
                    val imageUrl=it.replace("http://", "https://")
                    thumbnailUrls.add(imageUrl)
                }
            }catch (e: Exception){

            }
        }
        return thumbnailUrls
    }
}
