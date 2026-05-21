@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.bookshelfapp.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookshelfapp.AppViewModelProvider
import com.example.bookshelfapp.ui.BookShelfTopAppBar
import com.example.bookshelfapp.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookshelfapp.R
import com.example.bookshelfapp.network.VolumeInfo

object BookDetailsDestination: NavigationDestination{
    override val route="book_details"
    const val bookIdArg = "bookId"
    val routeWithArgs = "$route/{$bookIdArg}"
}

@Composable
fun BookDetailsScreen(
    modifier: Modifier= Modifier,
    navigateBack: () -> Unit,
    viewModel: BookDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { BookShelfTopAppBar(
            scrollBehavior = scrollBehavior,
            canNavigateBack = true,
            navigateUp = navigateBack
        ) }
    ) { paddingValues->
        when (val uiState = viewModel.bookDetailsUiState) {
            is BookDetailsUiState.Loading -> LoadingScreen(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            is BookDetailsUiState.Error -> ErrorScreen(
                retryAction = viewModel::getBookDetails,
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            is BookDetailsUiState.Success -> BookDetailsContent(
                volumeInfo = uiState.book.volumeInfo,
                modifier = modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun BookDetailsContent(
    volumeInfo: VolumeInfo?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(
                    volumeInfo?.imageLinks?.thumbnail?.replace("http://", "https://")
                )
                .crossfade(true)
                .build(),
            contentDescription = volumeInfo?.title,
            contentScale = ContentScale.Fit,
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            modifier = Modifier
                .height(260.dp)
                .fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = volumeInfo?.title ?: stringResource(R.string.unknown_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        volumeInfo?.authors?.let { authors ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = authors.joinToString(", "),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        volumeInfo?.publisher?.let {
            BookDetailRow(label = stringResource(R.string.publisher), value = it)
        }
        volumeInfo?.publishedDate?.let {
            BookDetailRow(label = stringResource(R.string.published_date), value = it)
        }
        volumeInfo?.pageCount?.let {
            BookDetailRow(label = stringResource(R.string.page_count), value = it.toString())
        }

        volumeInfo?.language?.let {
            BookDetailRow(label = stringResource(R.string.language), value = it)
        }
        volumeInfo?.categories?.let {
            BookDetailRow(
                label = stringResource(R.string.categories),
                value = it.joinToString(", ")
            )
        }

        volumeInfo?.description?.let { desc ->
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.description),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
private fun BookDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}