package com.example.unsplash.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PhotoEntity::class],
    version = 1
)
abstract class PhotosDatabase : RoomDatabase() {

    abstract val dao: PhotosDao
}