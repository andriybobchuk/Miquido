package com.example.unsplash.presentation.detail

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplash.domain.Photo
import com.example.unsplash.domain.Repository
import com.example.unsplash.presentation.util.asStringResource
import com.studios1299.playwall.core.domain.error_handling.SmartResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This VM uses some assumptions such as data on the server is not refreshed in realtime,
 * data from here is also not inserted into local db and so on. I did it to save time but general
 * requirements are met.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _photo = MutableStateFlow<Photo?>(null)
    val photo: StateFlow<Photo?> = _photo.asStateFlow()

    private val _errorMessages = MutableSharedFlow<Int>()
    val errorMessages: SharedFlow<Int> = _errorMessages.asSharedFlow()

    init {
        val photoId = savedStateHandle.get<String>("photoId")
        photoId?.let {
            loadPhotoDetails(it)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun loadPhotoDetails(photoId: String) {
        viewModelScope.launch {
            when (val result = repository.getPhotoDetails(photoId)) {
                is SmartResult.Success -> _photo.update { result.data }
                is SmartResult.Error -> {
                    val uiErrorMessage = result.error.asStringResource()
                    _errorMessages.emit(uiErrorMessage)
                }
            }
        }
    }
}
