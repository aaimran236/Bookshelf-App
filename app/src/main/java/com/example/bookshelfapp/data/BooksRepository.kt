package com.example.bookshelfapp.data


import com.example.bookshelfapp.network.BookApiService
import com.example.bookshelfapp.network.BookGridItem
import com.example.bookshelfapp.network.SpecificBookResponse

interface BookRepository {
    suspend fun getBooks(query: String): List<BookGridItem>
    suspend fun getBookDetails(bookId: String): SpecificBookResponse
}

class NetworkBookRepository(
    private val bookApiService: BookApiService
) : BookRepository {
    override suspend fun getBooks(query: String): List<BookGridItem> {
        val searchResponse = bookApiService.getBookIds(searchQuery = query)
        val books: MutableList<BookGridItem> = mutableListOf()

        searchResponse.items.orEmpty().forEach { item ->
            try {
                val bookInfo = bookApiService.getBookInfo(item.id)
                val thumbnail = bookInfo.volumeInfo?.imageLinks?.thumbnail
                    ?.replace("http://", "https://") ?: return@forEach
                val title = bookInfo.volumeInfo.title ?: return@forEach
                books.add(
                    BookGridItem(
                        id = item.id,
                        title = title,
                        thumbnailUrl = thumbnail
                    )
                )
            } catch (e: Exception) {
                // Skip books that fail individually
            }
        }
        return books
    }

    override suspend fun getBookDetails(bookId: String): SpecificBookResponse {
        return bookApiService.getBookInfo(bookId)
    }

}

/*
Previous method where we were fetching the book thumbnails only
override suspend fun getBookThumbnails(query: String): List<String> {
    val searchBookApiResponse = bookApiService.getBookIds(searchQuery = query)

    val thumbnailUrls: MutableList<String> = mutableListOf()

    searchBookApiResponse.items.orEmpty().forEach {
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
}*/