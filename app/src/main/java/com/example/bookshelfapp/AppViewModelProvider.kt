package com.example.bookshelfapp

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.bookshelfapp.ui.screens.BookDetailsViewModel
import com.example.bookshelfapp.ui.screens.home.BookShelfViewModel

/**
 * Central factory for all ViewModels in the app.
 *
 * Using a single factory object means every ViewModel is created the same way —
 * by pulling its dependencies from the application's AppContainer.
 * This replaces the companion object Factory that each ViewModel used to carry.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            BookShelfViewModel(
                bookshelfApplication().container.bookRepository
            )
        }
        initializer {
            BookDetailsViewModel(
                this.createSavedStateHandle(),
                bookshelfApplication().container.bookRepository
            )
        }
    }
}

/**
 * Extension function to retrieve the [BookSelfApplication] instance from
 * the factory's [CreationExtras]. Used by both ViewModel initializers above.
 */
fun CreationExtras.bookshelfApplication(): BookSelfApplication =
    checkNotNull(this[AndroidViewModelFactory.APPLICATION_KEY] as? BookSelfApplication) {
        "Application is not a BookSelfApplication — check your AndroidManifest.xml"
    }