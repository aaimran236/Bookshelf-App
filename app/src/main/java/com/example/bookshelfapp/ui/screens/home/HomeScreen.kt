@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookshelfapp.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookshelfapp.AppViewModelProvider
import com.example.bookshelfapp.R
import com.example.bookshelfapp.ui.BookShelfTopAppBar
import com.example.bookshelfapp.ui.navigation.NavigationDestination
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.paging.compose.collectAsLazyPagingItems

object HomeDestination : NavigationDestination{
    override val route= "Home"
}

/**
 * The Home screen composable. This is the only composable in this file that is
 * public — everything below it is a private implementation detail.
 *
 * It owns:
 *  - The Scaffold (TopAppBar, content area).
 *  - [searchBarText]: the text the user is currently typing. This is LOCAL UI
 *    state (rememberSaveable), not ViewModel state, because it is ephemeral —
 *    it has no meaning to the outside world until the user commits the search.
 *    rememberSaveable ensures it survives screen rotation.
 *  - The scroll behavior wired to nestedScroll so the TopAppBar hides on scroll.
 */
@Composable
fun HomeScreen(
    navigateToBookDetails: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookShelfViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // collectAsLazyPagingItems() subscribes to bookPagingFlow and returns a
    // LazyPagingItems object. Any time a new page loads or a load state changes,
    // Compose recomposes the relevant parts of the UI automatically.
    val lazyPagingItems = viewModel.bookPagingFlow.collectAsLazyPagingItems()

    // searchBarText is local UI state — it lives in this composable, not the ViewModel.
    // Initialized from the ViewModel's default query so the field is pre-populated.
    // rememberSaveable persists this across configuration changes (rotation, etc.).
    var searchBarText by rememberSaveable { mutableStateOf(BookShelfViewModel.Companion.DEFAULT_QUERY) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            BookShelfTopAppBar(
                title = stringResource(R.string.app_name),
                scrollBehavior = scrollBehavior,
                canNavigateBack = false,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            BookSearchBar(
                query = searchBarText,
                onQueryChange = { searchBarText = it },
                // Passes the current searchBarText to the ViewModel when the user
                // confirms. The ViewModel validates and commits it if appropriate.
                onSearch = { viewModel.onSearchTriggered(searchBarText) },
                modifier = Modifier.fillMaxWidth(),
            )

            // Delegates all paging state handling to a dedicated composable,
            // keeping this function focused on layout and navigation only.
            BooksPagingContent(
                lazyPagingItems = lazyPagingItems,
                onBookClick = navigateToBookDetails,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}


// These composables are internal (no private modifier) so BookDetailsScreen
// can reuse them without duplicating code.

/** Full-screen loading indicator, displayed during the initial page fetch. */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.loading),
            modifier = Modifier.size(200.dp),
        )
    }
}

/** Full-screen error message with a retry button. */
@Composable
fun ErrorScreen(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_connection_error),
            contentDescription = null,
        )
        Text(
            text = stringResource(R.string.loading_failed),
            modifier = Modifier.padding(16.dp),
        )
        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

