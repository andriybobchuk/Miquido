package com.example.unsplash.data.remote

import android.content.Context
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import androidx.paging.PagingState
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.RemoteMediator
import com.example.unsplash.data.local.PhotoEntity
import com.example.unsplash.data.local.PhotosDao
import com.example.unsplash.data.local.PhotosDatabase
import io.mockk.Runs
import io.mockk.just
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import java.io.IOException
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.unsplash.data.local.FakePhotosDatabase
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.fail

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalPagingApi::class)
class PhotosRemoteMediatorTest {

    private lateinit var mediator: PhotosRemoteMediator
    private lateinit var fakePhotosApi: FakePhotosApi
    private lateinit var fakePhotosDatabase: FakePhotosDatabase
    private lateinit var photosDao: PhotosDao

    @Before
    fun setup() {
        fakePhotosApi = FakePhotosApi()
        fakePhotosDatabase = FakePhotosDatabase()
        mediator = PhotosRemoteMediator(fakePhotosDatabase, fakePhotosApi)
    }

    @Test
    fun `load handles PREPEND correctly`() = runBlockingTest {
        val pagingState = createPagingState()

        val result = mediator.load(LoadType.PREPEND, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    private fun createPagingState(): PagingState<Int, PhotoEntity> {
        val config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 0,
            enablePlaceholders = true,
            initialLoadSize = 20
        )

        val items = List(40) { index ->
            PhotoEntity(
                id = "$index",
                author = "Author $index",
                width = 1920,
                height = 1080,
                url = "https://dummyimage.com/600x400/000/fff&text=$index",
                downloadUrl = "https://dummydownload.com/$index"
            )
        }

        val pages = listOf(
            PagingSource.LoadResult.Page(
                data = items.subList(0, 20),
                prevKey = null,
                nextKey = 1
            ),
            PagingSource.LoadResult.Page(
                data = items.subList(20, 40),
                prevKey = 0,
                nextKey = null
            )
        )

        val anchorPosition = 10

        return PagingState(
            pages = pages,
            anchorPosition = anchorPosition,
            config = config,
            leadingPlaceholderCount = 0
        )
    }
}
