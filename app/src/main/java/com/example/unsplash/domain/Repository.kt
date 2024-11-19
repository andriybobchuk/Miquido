package com.example.unsplash.domain

import androidx.paging.PagingData
import com.studios1299.playwall.core.domain.error_handling.DataError
import com.studios1299.playwall.core.domain.error_handling.SmartResult
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getPhotos(): Flow<PagingData<Photo>>
    suspend fun getPhotoDetails(photoId: String): SmartResult<Photo, DataError>
}