package com.example.bookshelfapp.fake

import com.example.bookshelfapp.network.BookApiService
import com.example.bookshelfapp.network.SearchBooksResponse
import com.example.bookshelfapp.network.SpecificBookResponse

class FakeBookApiService : BookApiService {
    override suspend fun getBookIds(searchQuery: String): SearchBooksResponse {
        return FakeDataSource.fakeBookResponse
    }

    override suspend fun getBookInfo(volumeId: String): SpecificBookResponse {
        return FakeDataSource.fakeBookInfoResponse[volumeId]
            ?: throw Exception("Book with id $volumeId not found")
    }

}