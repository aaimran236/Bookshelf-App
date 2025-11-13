package com.example.bookshelfapp

import com.example.bookshelfapp.fake.FakeBookThumbnailRepository
import com.example.bookshelfapp.fake.FakeDataSource
import com.example.bookshelfapp.rule.TestDispatcherRule
import com.example.bookshelfapp.ui.theme.screens.BookShelfViewModel
import com.example.bookshelfapp.ui.theme.screens.BookUiState
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class BookSelfViewModelTest {
    @get:Rule
    val dispatcher = TestDispatcherRule()

    @Test
    fun bookSelfViewModel_getThumbnails_verifyMarsUiStateSuccess(){
        val bookSelfViewModel: BookShelfViewModel = BookShelfViewModel(
            thumbnailRepository = FakeBookThumbnailRepository()
        )

        assertEquals(BookUiState.Success(FakeDataSource.thumbnailList),
            bookSelfViewModel.bookUiState)
    }
}