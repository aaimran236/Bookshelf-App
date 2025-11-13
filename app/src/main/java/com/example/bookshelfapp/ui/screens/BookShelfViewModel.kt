package com.example.bookshelfapp.ui.theme.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfapp.BookSelfApplication
import com.example.bookshelfapp.data.BookThumbnailRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


sealed interface BookUiState {
    data class Success(val thumbnailList: List<String>) : BookUiState
    object Error : BookUiState
    object Loading : BookUiState
}

class BookShelfViewModel(
    private val thumbnailRepository: BookThumbnailRepository
) : ViewModel() {

    var bookUiState: BookUiState by mutableStateOf(BookUiState.Loading)
        private set


    // The init block runs automatically every time an object of this class is created.
    // It's used for setup logic, like starting an initial data fetch.
    init {
        getBookThumbnails()
    }

    fun getBookThumbnails() {
        viewModelScope.launch {
            bookUiState = try {
//
                ///val bookThumbnailRepository: BookThumbnailRepository= NetworkBookThumbnailRepository()


                BookUiState.Success(thumbnailRepository.getBookThumbnails())
            } catch (e: IOException) {
                BookUiState.Error
            } catch (e: HttpException) {
                BookUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as BookSelfApplication)
                val bookThumbnailRepository = application.container.bookThumbnailRepository
                BookShelfViewModel(thumbnailRepository = bookThumbnailRepository)
            }
        }
    }
}