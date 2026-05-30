package com.example.bookshelfapp.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.bookshelfapp.network.BookApiService
import com.example.bookshelfapp.network.BookGridItem
import com.example.bookshelfapp.network.SpecificBookResponse
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for all book-related data operations.
 *
 * The ViewModel only ever talks to this interface, never to the network directly.
 * This makes it trivial to supply a FakeBookRepository in unit tests without
 * touching the network.
 */
interface BookRepository {

    /**
     * Returns a cold [Flow] of [PagingData] for the given [query].
     *
     * "Cold" means nothing happens until the Flow is collected. Each new
     * collection (triggered by a new search query via flatMapLatest) creates
     * an independent paging sequence starting at page 1.
     *
     * IMPORTANT: cachedIn() is deliberately NOT called here. It must be
     * called in the ViewModel using its viewModelScope, so the in-memory
     * page cache is tied to the ViewModel's lifecycle — not the repository's.
     */

    fun getBooksPagingFlow(query: String): Flow<PagingData<BookGridItem>>

    /** Fetches complete details for a single book identified by [bookId]. */
    suspend fun getBookDetails(bookId: String): SpecificBookResponse
}

/**
 * The real, production implementation of [BookRepository].
 * Fetches all data from the network via [bookApiService].
 */
class NetworkBookRepository(
    private val bookApiService: BookApiService,
) : BookRepository {

    override fun getBooksPagingFlow(query: String): Flow<PagingData<BookGridItem>> =
        Pager(
            config = PagingConfig(
                pageSize = BookPagingSource.PAGE_SIZE,

                // Without this, Paging would request 3× PAGE_SIZE items on the
                // first load (60 items). The Google Books API caps maxResults at 40,
                // so we align initialLoadSize with pageSize to stay within limits.
                initialLoadSize = BookPagingSource.PAGE_SIZE,

                // Placeholders show empty grey boxes while a page is loading.
                // Disabled here because we don't know the total result count upfront,
                // which is required to size the placeholder list correctly.
                enablePlaceholders = false,
            ),

            // pagingSourceFactory is a lambda (not a single instance) because
            // a new PagingSource must be created for each fresh load — a single
            // reused instance would carry stale state from the previous query.
            pagingSourceFactory = { BookPagingSource(bookApiService, query) },
        ).flow

    override suspend fun getBookDetails(bookId: String): SpecificBookResponse =
        bookApiService.getBookDetails(bookId)
}

/*
without using paging
class NetworkBookRepository(
    private val bookApiService: BookApiService
) : BookRepository {
    override suspend fun getBooks(query: String,maxResults: Int): List<BookGridItem> {
        val searchResponse = bookApiService.getBookIds(searchQuery = query, maxResults = maxResults)
        val books: MutableList<BookGridItem> = mutableListOf()

        searchResponse.items.orEmpty().forEach { item ->
            try {
                ///val bookInfo = bookApiService.getBookInfo(item.id)
                val thumbnail = item.volumeInfo?.imageLinks?.thumbnail
                    ?.replace("http://", "https://") ?: return@forEach
                val title = item.volumeInfo.title ?: return@forEach
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
*/