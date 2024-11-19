package com.example.unsplash.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PhotosDao {

    @Upsert
    suspend fun upsertAll(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photoentity WHERE id = :photoId")
    suspend fun getPhotoById(photoId: String): PhotoEntity?

    @Query("SELECT * FROM photoentity")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Query("SELECT * FROM photoentity")
    fun pagingSource(): PagingSource<Int, PhotoEntity>

    @Query("DELETE FROM photoentity")
    suspend fun clearAll()
}