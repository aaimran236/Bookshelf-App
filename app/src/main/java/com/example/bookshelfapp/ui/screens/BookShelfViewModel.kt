package com.example.bookshelfapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.data.BookRepository
import com.example.bookshelfapp.network.BookGridItem
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface BookUiState {
    data class Success(val books: List<BookGridItem>) : BookUiState
    object Error : BookUiState
    object Loading : BookUiState
}

class BookShelfViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {

    var bookUiState: BookUiState by mutableStateOf(BookUiState.Loading)
        private set

    var searchQuery: String by mutableStateOf("jazz history")
        private set


    // The init block runs automatically every time an object of this class is created.
    // It's used for setup logic, like starting an initial data fetch.

    init {
        getBookThumbnails()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun getBookThumbnails() {
        viewModelScope.launch {
            bookUiState = BookUiState.Loading
            bookUiState = try {
                ///val bookThumbnailRepository: BookThumbnailRepository= NetworkBookThumbnailRepository()
                BookUiState.Success(bookRepository.getBooks(query=searchQuery, maxResults = 20))
            } catch (e: IOException) {
                BookUiState.Error
            } catch (e: HttpException) {
                BookUiState.Error
            }
        }
    }

}