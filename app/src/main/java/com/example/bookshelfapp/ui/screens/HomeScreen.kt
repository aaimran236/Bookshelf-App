package com.example.bookshelfapp.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.bookshelfapp.R

@Composable
fun HomeScreen(
    bookUiState: BookUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
){
    when(bookUiState){
        is BookUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is BookUiState.Success -> ResultScreen(
            bookUiState.ids, modifier = modifier.fillMaxWidth()
        )

        is BookUiState.Error -> ErrorScreen(modifier = modifier.fillMaxSize())
    }
}

@Composable
private fun MarsPhotoCard(///photo: MarsPhoto,
 modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        ///TODO: add AsyncImage  & Text for title
        ///            AsyncImage(
        //                model = ImageRequest.Builder(context = LocalContext.current)
        //                    .data(photo.imgSrc)
        //                    .crossfade(true)
        //                    .build(),
        //                contentDescription = stringResource(R.string.mars_photo),
        //                contentScale = ContentScale.Crop,
        //                error = painterResource(R.drawable.ic_broken_image),
        //                placeholder = painterResource(R.drawable.loading_img),
        //                modifier = Modifier.fillMaxWidth(),
        //            )


    }
}

@Composable
fun ResultScreen(bookId: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Text(text = bookId)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
    }
}