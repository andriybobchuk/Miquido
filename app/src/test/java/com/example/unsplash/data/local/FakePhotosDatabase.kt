package com.example.unsplash.data.local

import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class FakePhotosDatabase : PhotosDatabase() {
    private val photosDao = FakePhotosDao()

    override val dao: PhotosDao
        get() = photosDao


    override fun clearAllTables() {
        runBlocking {
            photosDao.clearAll()
        }
    }

    override fun createInvalidationTracker(): InvalidationTracker {
        return InvalidationTracker(this, "photoentity")
    }

    override fun createOpenHelper(config: DatabaseConfiguration): SupportSQLiteOpenHelper {
        return FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(config.context)
                .name(config.name ?: "test-database")
                .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {

                    }
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {

                    }
                })
                .build()
        )
    }
}
