package com.example.unsplash.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.unsplash.domain.Photo
import com.example.unsplash.domain.Repository
import com.studios1299.playwall.core.domain.error_handling.DataError
import com.studios1299.playwall.core.domain.error_handling.SmartResult
import kotlinx.coroutines.test.TestCoroutineDispatcher
import androidx.lifecycle.SavedStateHandle
import com.example.unsplash.presentation.util.asStringResource
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

@ExperimentalCoroutinesApi
class DetailViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel
    private lateinit var repository: Repository
    private lateinit var savedStateHandle: SavedStateHandle
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        savedStateHandle = mockk(relaxed = true)
        coEvery { savedStateHandle.get<String>("photoId") } returns "1"
        repository = mockk(relaxed = true)
        viewModel = DetailViewModel(repository, savedStateHandle)
    }

    @Test
    fun `loadPhotoDetails emits photo on success`() = runTest {
        val expectedPhoto = Photo("1", "Author", 1920, 1080, "url", "url")
        coEvery { repository.getPhotoDetails("1") } returns SmartResult.Success(expectedPhoto)

        viewModel.loadPhotoDetails("1")

        val photo = viewModel.photo.first()
        assertEquals(expectedPhoto, photo)
    }

    @Test
    fun `loadPhotoDetails emits no internet error message on failure`() = testDispatcher.runBlockingTest {
        val expectedError = DataError.Network.NO_INTERNET
        coEvery { repository.getPhotoDetails("1") } returns SmartResult.Error(expectedError)
        val errors = mutableListOf<Int>()
        val job = launch {
            viewModel.errorMessages.collect { errors.add(it) }
        }
        viewModel.loadPhotoDetails("1")
        advanceUntilIdle()
        assertTrue("Expected at least one error message", errors.isNotEmpty())
        assertEquals(expectedError.asStringResource(), errors.first())

        job.cancel()
    }

    @Test
    fun `loadPhotoDetails emits other error message on failure`() = testDispatcher.runBlockingTest {
        val expectedError = DataError.Network.SERVER_ERROR
        coEvery { repository.getPhotoDetails("1") } returns SmartResult.Error(expectedError)
        val errors = mutableListOf<Int>()
        val job = launch {
            viewModel.errorMessages.collect { errors.add(it) }
        }
        viewModel.loadPhotoDetails("1")
        advanceUntilIdle()
        assertTrue("Expected at least one error message", errors.isNotEmpty())
        assertEquals(expectedError.asStringResource(), errors.first())

        job.cancel()
    }
}
