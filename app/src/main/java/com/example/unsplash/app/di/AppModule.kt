package com.example.unsplash.app.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.unsplash.data.RepositoryImpl
import com.example.unsplash.data.local.PhotoEntity
import com.example.unsplash.data.local.PhotosDatabase
import com.example.unsplash.data.remote.PhotosApi
import com.example.unsplash.data.remote.PhotosRemoteMediator
import com.example.unsplash.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePhotosDatabase(@ApplicationContext context: Context): PhotosDatabase {
        return Room.databaseBuilder(
            context,
            PhotosDatabase::class.java,
            "photos.db"
        ).build()
    }

    @Provides
    @Singleton
    fun providePhotosApi(): PhotosApi {
        return Retrofit.Builder()
            .baseUrl(PhotosApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun providePhotosPager(photosDatabase: PhotosDatabase, photosApi: PhotosApi): Pager<Int, PhotoEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 0, // Typically I would make it pageSize but i adjusted it for your requirements
                initialLoadSize = 20 // Again, I would leave the default pageSize*3 but adjusted it for your requirements
            ),
            remoteMediator = PhotosRemoteMediator(
                photosDatabase = photosDatabase,
                photosApi = photosApi
            ),
            pagingSourceFactory = {
                photosDatabase.dao.pagingSource()
            }
        )
    }

    @Provides
    @Singleton
    fun providePhotosRepository(
        photosDatabase: PhotosDatabase,
        photosApi: PhotosApi,
        pager: Pager<Int, PhotoEntity>
    ): Repository = RepositoryImpl(photosDatabase, photosApi, pager)
}