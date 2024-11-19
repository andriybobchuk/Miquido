package com.example.unsplash.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import coil.network.HttpException
import com.example.unsplash.data.local.PhotoEntity
import com.example.unsplash.data.local.PhotosDatabase
import com.example.unsplash.data.mappers.toEntity
import kotlinx.coroutines.delay
import okio.IOException
import java.net.UnknownHostException

/**
 * This is where I handle all of my pagination as you can see.
 * @see [AppModule] for some config info too (providePhotosPager)
 */
@OptIn(ExperimentalPagingApi::class)
class PhotosRemoteMediator(
    private val photosDatabase: PhotosDatabase,
    private val photosApi: PhotosApi
) : RemoteMediator<Int, PhotoEntity>() {

    companion object {
        private const val LOG_TAG = "PhotosRemoteMediator"
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {

        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    1
                }

                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )

                LoadType.APPEND -> {
                    Log.d(LOG_TAG, "Load type = APPEND")
                    val lastItem = state.lastItemOrNull()

                    if (lastItem == null) {
                        Log.d(LOG_TAG, "last item == null, this is the initial load for page 1")
                        1
                    } else {
                        Log.d(
                            LOG_TAG,
                            "this is not the initial load -> load started with last load key = ${lastItem.id.toInt()}"
                        )
                        delay(4000) // TODO("I slowed it down on purpose so you could see the loading indicator after each 20 items! Normally this delay woundt be here")

                        ((lastItem.id.toInt() + 1) / state.config.pageSize) + 1
                    }
                }
            }
            Log.d(LOG_TAG, "Newly calculated load key = $loadKey")

            val photos = photosApi.getPhotos(
                page = loadKey,
                limit = state.config.pageSize
            )

            photosDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photosDatabase.dao.clearAll()
                }
                val photoEntities = photos.map { it.toEntity() }
                photosDatabase.dao.upsertAll(photoEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = photos.isEmpty()
            )
        } catch (e: UnknownHostException) {
            Log.e(LOG_TAG, "No Internet Connection")
            MediatorResult.Error(Throwable("No Internet Connection"))
        } catch (e: IOException) {
            Log.e(LOG_TAG, "IOException: $e")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(LOG_TAG, "HttpException: $e")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "Unknown exception: $e")
            MediatorResult.Error(e)
        }
    }
}