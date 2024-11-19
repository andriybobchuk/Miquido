package com.example.unsplash.data.remote

import retrofit2.Response

class FakePhotosApi : PhotosApi {
    private val photos = mutableListOf<PhotoDto>()

    init {
        repeat(300) { index ->
            photos.add(
                PhotoDto(
                    id = "$index",
                    author = "Author $index",
                    width = 1920,
                    height = 1080,
                    url = "https://dummyimage.com/600x400/000/fff&text=Photo+$index",
                    download_url = "https://dummydownload.com/photo_$index"
                )
            )
        }
    }

    override suspend fun getPhotos(page: Int, limit: Int): List<PhotoDto> {
        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, photos.size)
        val sublist = if (startIndex < photos.size) photos.subList(startIndex, endIndex) else emptyList()
        println("getPhotos called: startIndex=$startIndex, endIndex=$endIndex, returnedSize=${sublist.size}")
        return sublist
    }

    override suspend fun getPhotoDetails(id: String): Response<PhotoDto> {
        val photo = photos.find { it.id == id }
        return if (photo != null) {
            Response.success(photo)
        } else {
            Response.error(404, okhttp3.ResponseBody.create(null, "Not found"))
        }
    }

    companion object {
        const val BASE_URL = "https://picsum.photos/v2/"
    }
}
