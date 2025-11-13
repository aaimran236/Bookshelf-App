package com.example.bookshelfapp

import com.example.bookshelfapp.data.BookThumbnailRepository
import com.example.bookshelfapp.data.NetworkBookThumbnailRepository
import com.example.bookshelfapp.fake.FakeBookApiService
import com.example.bookshelfapp.fake.FakeDataSource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test


class NetworkBookThumbnailRepositoryTest {
    @Test
    fun networkBookThumbnailRepository_getBookThumbnails_verifyThumbnails()= runTest{
        val bookThumbnailRepository: BookThumbnailRepository= NetworkBookThumbnailRepository(
            bookApiService = FakeBookApiService()
        )


        assertEquals(FakeDataSource.thumbnailList,bookThumbnailRepository.getBookThumbnails())
    }
}