package com.example.unsplash.data.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotosApi {

    @GET("list")
    suspend fun getPhotos(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): List<PhotoDto>

    @GET("id/{id}/info")
    suspend fun getPhotoDetails(
        @Path("id") id: String
    ): Response<PhotoDto>

    companion object {
        const val BASE_URL = "https://picsum.photos/v2/"
    }
}