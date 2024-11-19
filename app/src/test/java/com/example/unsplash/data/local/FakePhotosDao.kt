package com.example.unsplash.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState

class FakePhotosDao : PhotosDao {
    private val photos = mutableListOf<PhotoEntity>()

    override suspend fun upsertAll(photos: List<PhotoEntity>) {
        val map = this.photos.associateBy { it.id }.toMutableMap()
        photos.forEach { map[it.id] = it }
        this.photos.clear()
        this.photos.addAll(map.values)
    }

    override suspend fun getPhotoById(photoId: String): PhotoEntity? {
        return photos.find { it.id == photoId }
    }

    override suspend fun getAllPhotos(): List<PhotoEntity> {
        return photos.toList()
    }

    override fun pagingSource(): PagingSource<Int, PhotoEntity> {
        return FakePagingSource(photos)
    }

    override suspend fun clearAll() {
        photos.clear()
    }
}


class FakePagingSource(private val photos: List<PhotoEntity>) : PagingSource<Int, PhotoEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PhotoEntity> {
        val position = params.key ?: 0

        val loadSize = params.loadSize
        val start = position * loadSize
        val end = minOf((position + 1) * loadSize, photos.size)

        val prevKey = if (position == 0) null else position - 1
        val nextKey = if (end == photos.size) null else position + 1

        return try {
            val data = photos.subList(start, end)
            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IndexOutOfBoundsException) {
            LoadResult.Page(
                data = emptyList(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PhotoEntity>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val page = anchorPosition / state.config.pageSize
            if (state.closestItemToPosition(anchorPosition) == null) page + 1 else page
        }
    }
}
