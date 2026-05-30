package com.example.bookshelfapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelfapp.R
import com.example.bookshelfapp.network.BookGridItem

/**
 * Routes the UI to the correct composable based on the current paging state.
 *
 * loadState.refresh represents the state of the most recent full data load:
 *  - The very first load after the screen opens.
 *  - A full reload triggered when the user searches a new query.
 *
 * This is distinct from loadState .append (handled inside BooksGrid), which
 * represents subsequent page fetches as the user scrolls.
 */
@Composable
fun BooksPagingContent(
    lazyPagingItems: LazyPagingItems<BookGridItem>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (val refreshState = lazyPagingItems.loadState.refresh) {

        is LoadState.Loading -> {
            // Full-screen spinner for the initial/refresh load.
            LoadingScreen(modifier = modifier)
        }

        is LoadState.Error -> {
            // Full-screen error with retry for the initial/refresh failure.
            // lazyPagingItems.retry() re-triggers the last failed load request.
            ErrorScreen(
                onRetry = { lazyPagingItems.retry() },
                modifier = modifier,
            )
        }

        is LoadState.NotLoading -> {
            when {
                lazyPagingItems.itemCount == 0 -> {
                    // The load completed successfully but returned zero items.
                    EmptyResultsScreen(modifier = modifier)
                }
                else -> {
                    BooksGrid(
                        lazyPagingItems = lazyPagingItems,
                        onBookClick = onBookClick,
                        modifier = modifier,
                    )
                }
            }
        }
    }
}

// ─── Grid ─────────────────────────────────────────────────────────────────────

/**
 * Renders the book grid using [LazyVerticalGrid].
 *
 * The [items] block uses lazyPagingItems.itemCount and lazyPagingItems[index]
 * instead of a List because [LazyPagingItems] notifies the Pager when items
 * near the end are accessed, which is what triggers loading the next page.
 *
 * A full-width footer item at the end handles the append (next page) load state:
 * a spinner while loading, a retry button on failure, or an end-of-list label
 * when all pages have been loaded.
 */
@Composable
private fun BooksGrid(
    lazyPagingItems: LazyPagingItems<BookGridItem>,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            count = lazyPagingItems.itemCount,
            // A stable key prevents Composes from recreating composables
            // unnecessarily when the list is updated with new pages.
            key = lazyPagingItems.itemKey { it.id },
        ) { index ->
            // lazyPagingItems[index] accessing the item also signals the Pager
            // that this index has been requested — the trigger for loading the
            // next page when the user scrolls near the end.
            // The ?.let safely skips null items (can occur with placeholders,
            // though we have enablePlaceholders = false).
            lazyPagingItems[index]?.let { book ->
                BookCard(
                    book = book,
                    onClick = { onBookClick(book.id) },
                )
            }
        }

        // GridItemSpan(maxLineSpan) makes this single item span all columns.
        // This is how you put a full-width footer inside a LazyVerticalGrid.
        item(span = { GridItemSpan(maxLineSpan) }) {
            AppendLoadStateFooter(
                appendState = lazyPagingItems.loadState.append,
                onRetry = { lazyPagingItems.retry() },
            )
        }
    }
}

// ─── Append Footer ────────────────────────────────────────────────────────────

/**
 * Displays the appropriate UI at the bottom of the grid depending on the
 * state of the NEXT PAGE load (append), which is separate from the initial load.
 *
 * Three states:
 *  Loading     → spinner (next page is being fetched)
 *  Error       → inline retry button (next page fetch failed)
 *  NotLoading  → end-of-results label when all pages are exhausted,
 *                nothing otherwise (more pages are available but not yet triggered)
 */
@Composable
private fun AppendLoadStateFooter(
    appendState: LoadState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (appendState) {
        is LoadState.Loading -> {
            CircularProgressIndicator(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally),
            )
        }

        is LoadState.Error -> {
            // OutlinedButton (rather than the filled Button used in ErrorScreen)
            // because this is a secondary, in-list retry — less visually prominent.
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.loading_failed),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
                OutlinedButton(onClick = onRetry) {
                    Text(stringResource(R.string.retry))
                }
            }
        }

        is LoadState.NotLoading -> {
            // endOfPaginationReached is true when nextKey was null in the last
            // LoadResult.Page — meaning the API has no more pages to offer.
            if (appendState.endOfPaginationReached) {
                Text(
                    text = stringResource(R.string.end_of_results),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

// ─── Search Bar ───────────────────────────────────────────────────────────────

/**
 * A search input field with a search icon button on the right.
 *
 * Two ways to trigger the search:
 *  1. Tap the trailing search icon (IconButton).
 *  2. Press the Search key on the keyboard (ImeAction.Search).
 *
 * Both paths hide the keyboard after triggering.
 */
@Composable
fun BookSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    // Wraps onSearch to also hide the keyboard — avoids duplicating the
    // keyboard.hide() call in both the IconButton and KeyboardActions lambdas.
    val onSearchAndHideKeyboard : ()->Unit= {
        onSearch()
        keyboardController?.hide()
    }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(text = stringResource(R.string.search_placeholder)) },
        trailingIcon = {
            IconButton(onClick = onSearchAndHideKeyboard) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchAndHideKeyboard() }),
        shape = RoundedCornerShape(50.dp),
    )
}

// ─── Book Card ────────────────────────────────────────────────────────────────

/**
 * A single book card showing the cover thumbnail and title.
 * Tapping it triggers [onClick], which navigates to the details screen.
 */
@Composable
private fun BookCard(
    book: BookGridItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(book.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_broken_image),
                placeholder = painterResource(R.drawable.loading_img),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f),
            )
            Text(
                text = book.title,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            )
        }
    }
}

/** Full-screen message shown when a search returns zero results. */
@Composable
private fun EmptyResultsScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.no_results),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}