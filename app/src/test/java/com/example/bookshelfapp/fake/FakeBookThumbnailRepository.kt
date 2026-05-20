package com.example.bookshelfapp.fake

import com.example.bookshelfapp.data.BookThumbnailRepository

class FakeBookThumbnailRepository : BookThumbnailRepository {
    override suspend fun getBookThumbnails(query: String): List<String> {
        return FakeDataSource.thumbnailList
    }
}