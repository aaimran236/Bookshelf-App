package com.example.bookshelfapp

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfapp.ui.screens.BookDetailsViewModel
import com.example.bookshelfapp.ui.screens.BookShelfViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            BookShelfViewModel(
                bookSelfApplication().container.bookRepository
            )
        }
        initializer {
            BookDetailsViewModel(
                this.createSavedStateHandle(),
                bookSelfApplication().container.bookRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [BookSelfApplication].
 */

fun CreationExtras.bookSelfApplication(): BookSelfApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BookSelfApplication)