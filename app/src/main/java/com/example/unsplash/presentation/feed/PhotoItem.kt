package com.example.unsplash.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.unsplash.R
import com.example.unsplash.domain.Photo

@Composable
fun PhotoItem(
    photo: Photo,
    modifier: Modifier = Modifier,
    onClick: (id: String) -> Unit
) {
    var imageLoadingState by remember { mutableStateOf(ImageLoadingState.Loading) }

    Box(modifier = modifier) {
        AsyncImage(
            model = photo.downloadUrl,
            contentDescription = stringResource(R.string.photo_id, photo.id),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .then(if (imageLoadingState == ImageLoadingState.Success) Modifier.clickable { onClick(photo.id) } else Modifier),
            contentScale = ContentScale.Crop,
            onLoading = {
                imageLoadingState = ImageLoadingState.Loading
            },
            onSuccess = {
                imageLoadingState = ImageLoadingState.Success
            },
            onError = {
                imageLoadingState = ImageLoadingState.Error
            }
        )

        when (imageLoadingState) {
            ImageLoadingState.Loading -> {
                // Would be nice to put some beautiful animation but white background is also beautiful
            }
            ImageLoadingState.Error -> {
                Column(modifier = Modifier.matchParentSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\uD83D\uDE14",
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = stringResource(R.string.image_failed, photo.id),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = stringResource(R.string.check_your_internet_connection),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            ImageLoadingState.Success -> {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = photo.id,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

enum class ImageLoadingState {
    Loading,
    Success,
    Error
}