package com.example.bookshelfapp.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.data.BookRepository
import com.example.bookshelfapp.network.SpecificBookResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface BookDetailsUiState {
    data class Success(val book: SpecificBookResponse) : BookDetailsUiState
    object Error : BookDetailsUiState
    object Loading : BookDetailsUiState
}


class BookDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository
): ViewModel() {
    private val bookId: String =
        checkNotNull(savedStateHandle[BookDetailsDestination.bookIdArg])

    var bookDetailsUiState: BookDetailsUiState by mutableStateOf(BookDetailsUiState.Loading)
        private set

    init {
        getBookDetails()
    }

    fun getBookDetails() {
        viewModelScope.launch {
            bookDetailsUiState = BookDetailsUiState.Loading
            bookDetailsUiState = try {
                BookDetailsUiState.Success(bookRepository.getBookDetails(bookId))
            } catch (e: IOException) {
                BookDetailsUiState.Error
            } catch (e: HttpException) {
                BookDetailsUiState.Error
            }
        }
    }
}