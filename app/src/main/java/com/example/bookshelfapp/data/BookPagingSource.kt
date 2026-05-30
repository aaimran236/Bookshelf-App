package com.example.bookshelfapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.bookshelfapp.network.BookApiService
import com.example.bookshelfapp.network.BookGridItem
import com.example.bookshelfapp.network.BookItem
import retrofit2.HttpException
import java.io.IOException

/**
 * Loads pages of [BookGridItem] from the Google Books API.
 *
 * Generic parameters explained:
 *   PagingSource<Key, Value>
 *   Key   = Int         → the startIndex (0, 20, 40, 60 …)
 *   Value = BookGridItem → one card shown in the grid
 *
 * The Paging library creates a fresh instance of this class every time
 * the user performs a new search (via the pagingSourceFactory lambda
 * in the repository). This ensures a clean, independent page sequence
 * for each query.
 */

class BookPagingSource(
    private val bookApiService: BookApiService,
    private val query: String,
) : PagingSource<Int, BookGridItem>() {

    companion object {
        /**
         * Items requested per page. Kept to 20 because the Google Books API
         * enforces a maximum of 40, and the Paging library loads 3× pageSize
         * on the first call by default. Setting initialLoadSize = PAGE_SIZE
         * in PagingConfig (see repository) prevents this from exceeding 40.
         */
        const val PAGE_SIZE = 20

        /** The offset of the very first page. Google Books API is 0-based. */
        private const val STARTING_INDEX = 0
    }

    /**
     * The Paging library calls this automatically whenever it decides more data
     * is needed — either for the initial load or when the user scrolls near
     * the bottom of the list.
     *
     * [LoadParams.key] is the startIndex:
     *   null → first ever load  → use STARTING_INDEX (0)
     *   20   → second page      → load items 20–39
     *   40   → third page       → load items 40–59, and so on.
     *
     * [LoadParams.loadSize] is intentionally ignored. We always request
     * exactly PAGE_SIZE items to keep our API calls predictable.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookGridItem> {
        val startIndex = params.key ?: STARTING_INDEX

        return try {
            val response = bookApiService.searchBooks(
                searchQuery = query,
                startIndex = startIndex,
                maxResults = PAGE_SIZE,
            )

            // mapNotNull converts each raw BookItem to a BookGridItem,
            // and silently drops any item that is missing a title or thumbnail.
            val books = response.items
                .orEmpty()
                .mapNotNull { it.toBookGridItemOrNull() }

            LoadResult.Page(
                data = books,

                // prevKey — the startIndex that would load the page BEFORE this one.
                // null when startIndex == 0 because there is no previous page.
                prevKey = if (startIndex == STARTING_INDEX) null
                else startIndex - PAGE_SIZE,

                // nextKey — the startIndex that would load the page AFTER this one.
                // null when books is empty signals Paging to stop requesting more pages.
                // Using books.size (actual items received) rather than PAGE_SIZE correctly
                // handles the final page, which may have fewer items than PAGE_SIZE.
                nextKey = if (books.isEmpty()) null
                else startIndex + books.size,
            )

        } catch (exception: IOException) {
            // Network-level failures: no connection, timeout, DNS error.
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            // HTTP-level failures: 403 Forbidden, 429 Rate Limit, 500 Server Error.
            LoadResult.Error(exception)
        }
    }

    /**
     * Called when Paging needs to REFRESH the data — e.g. after retrying a
     * failed load, or when the data source signals it is stale.
     *
     * It returns the startIndex closest to [state.anchorPosition] (the index of
     * the item currently visible on screen), so after a refresh the user stays
     * near where they were rather than jumping back to page 1.
     *
     * Idiomatic Kotlin null-safe chaining replaces the verbose if/else approach.
     */
    override fun getRefreshKey(state: PagingState<Int, BookGridItem>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { anchorPage ->
                anchorPage.prevKey?.plus(PAGE_SIZE)
                    ?: anchorPage.nextKey?.minus(PAGE_SIZE)
            }
        }
}


/**
 * Converts a raw network [BookItem] into a display-ready [BookGridItem].
 *
 * Kept private to this file because it is an implementation detail of the
 * PagingSource. The network layer (BookItem) should not know about the UI
 * model (BookGridItem), and the UI model should not know about network models.
 *
 * Returns null if either title or thumbnail is absent, so the caller can use
 * mapNotNull to cleanly skip incomplete entries without try/catch.
 */
private fun BookItem.toBookGridItemOrNull(): BookGridItem? {
    val title = volumeInfo?.title ?: return null
    val thumbnail = volumeInfo.imageLinks?.thumbnail
        ?.replace("http://", "https://")   // The API returns http://, Android requires https://
        ?: return null

    return BookGridItem(
        id = id,
        title = title,
        thumbnailUrl = thumbnail,
    )
}