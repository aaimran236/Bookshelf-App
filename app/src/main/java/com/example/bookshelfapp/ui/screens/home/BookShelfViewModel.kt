package com.example.bookshelfapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.bookshelfapp.data.BookRepository
import com.example.bookshelfapp.network.BookGridItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class BookShelfViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    companion object {
        /** The query shown and searched on first launch. */
        const val DEFAULT_QUERY = "jazz history"
    }

    /**
     * The query currently driving the Pager.
     *
     * Private mutable version: only this ViewModel can change the value.
     * Public immutable version (activeQuery): the UI observes this as a
     * read-only stream — it can never push values back into the ViewModel
     * directly, enforcing unidirectional data flow.
     */
    private val _activeQuery = MutableStateFlow(DEFAULT_QUERY)
    val activeQuery: StateFlow<String> = _activeQuery.asStateFlow()

    /**
     * The paginated stream of books to display in the grid.
     *
     * Flow pipeline explained step by step:
     *
     * 1. _activeQuery emits a new string whenever the user triggers a search.
     *
     * 2. flatMapLatest reacts to each emission by:
     *    a. Cancelling the previous paging Flow (and its in-flight network requests).
     *    b. Starting a brand-new Flow from getBooksPagingFlow(newQuery).
     *    This is what resets pagination to page 1 on every new search.
     *
     * 3. cachedIn(viewModelScope) stores the pages already loaded in memory,
     *    keyed to the ViewModel's scope. Without this:
     *    - Rotating the screen would discard all loaded pages and re-fetch.
     *    - Navigating to BookDetailsScreen and back would restart from page 1.
     *    With it, the grid restores instantly from the in-memory cache.
     *
     * @OptIn scoped to the property (not the class) so only this usage
     * opts into the experimental API, not everything in the ViewModel.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val bookPagingFlow: Flow<PagingData<BookGridItem>> = _activeQuery
        .flatMapLatest { query -> bookRepository.getBooksPagingFlow(query) }
        .cachedIn(viewModelScope)

    /**
     * Called when the user confirms their search (search icon tap or keyboard
     * Search action).
     *
     * Guards:
     *  - Trims leading/trailing whitespace before committing.
     *  - Ignores blank queries (empty or whitespace-only).
     *  - Ignores queries identical to the current one to avoid an unnecessary
     *    network reset when the user taps Search without changing anything.
     */
    fun onSearchTriggered(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank() || trimmedQuery == _activeQuery.value) return
        _activeQuery.value = trimmedQuery
    }
}