package com.example.unsplash.presentation.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.unsplash.data.local.PhotoEntity
import com.example.unsplash.data.mappers.toPhoto
import com.example.unsplash.domain.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * You may ask me why it's so empty? well because [PagingData] holds all the necessary state info
 * about paging so we dont need any custom solutions for error handling or paging state.
 */
@HiltViewModel
class FeedViewModel @Inject constructor(
    repository: Repository
): ViewModel() {

    val photosPagingFlow = repository.getPhotos().cachedIn(viewModelScope)
}