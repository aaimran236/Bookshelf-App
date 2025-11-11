package com.example.bookshelfapp.ui.theme.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshelfapp.network.BookApi
import kotlinx.coroutines.launch
import java.io.IOException


sealed interface BookUiState {
    data class Success(val ids: String) : BookUiState
    object Error : BookUiState
    object Loading : BookUiState
}

class BookShelfViewModel : ViewModel() {

    var bookUiState: BookUiState by mutableStateOf(BookUiState.Loading)
        private set


    init {
        getBookIds()
    }

   fun getBookIds(){
       viewModelScope.launch {
           bookUiState=try {
               val bookResponse= BookApi.retrofitService.getBookIds()
               BookUiState.Success("Success: ${bookResponse.items.size} Book ids retrieved")
           }catch (e: IOException){
               BookUiState.Error
           }
       }
    }
}