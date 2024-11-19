package com.example.unsplash.data


import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.example.unsplash.data.local.PhotoEntity
import com.example.unsplash.data.local.PhotosDatabase
import com.example.unsplash.data.mappers.toEntity
import com.example.unsplash.data.mappers.toPhoto
import com.example.unsplash.data.networking.RetrofitClientExt
import com.example.unsplash.data.remote.PhotosApi
import com.example.unsplash.domain.Photo
import com.example.unsplash.domain.Repository
import com.studios1299.playwall.core.domain.error_handling.DataError
import com.studios1299.playwall.core.domain.error_handling.SmartResult
import com.studios1299.playwall.core.domain.error_handling.fold
import com.studios1299.playwall.core.domain.error_handling.onError
import com.studios1299.playwall.core.domain.error_handling.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * You may notice that the ways I retrieve and handle data for getPhotos() and getPhotoDetails() are
 * extremely different. The reason for that is that for getPhotos() I use Paging3 library which
 * delegates a lot of work to its remote mediator while getPhotoDetails() is not using it so I had
 * to implement custom error handling and generally more logic on every level.
 *
 * @see [PhotosRemoteMediator] performs paging operations which allowed to keep getPhotos() so compact.
 */
class RepositoryImpl @Inject constructor(
    private val photosDatabase: PhotosDatabase,
    private val photosApi: PhotosApi,
    private val pager: Pager<Int, PhotoEntity>
) : Repository {

    companion object {
        private const val LOG_TAG = "RepositoryImpl"
    }

    override fun getPhotos(): Flow<PagingData<Photo>> {
        return pager.flow.map { pagingData ->
            pagingData.map { it.toPhoto() }
        }
    }

    override suspend fun getPhotoDetails(photoId: String): SmartResult<Photo, DataError> {
        photosDatabase.dao.getPhotoById(photoId)?.let { photoEntity ->
            Log.d(LOG_TAG, "getPhotoDetails(), photoEntity from local: $photoEntity")
            return SmartResult.Success(photoEntity.toPhoto())
        }

        return RetrofitClientExt.safeCall {
            photosApi.getPhotoDetails(photoId)
        }.fold(
            onSuccess = { photoDto ->
                Log.i(LOG_TAG, "getPhotoDetails(), photoDto from remote: $photoDto")
                SmartResult.Success(photoDto.toEntity().toPhoto())
            },
            onError = { error ->
                Log.e(LOG_TAG, "Error fetching photo details: $error")
                SmartResult.Error(error as? DataError ?: DataError.Network.UNKNOWN)
            }
        )
    }
}