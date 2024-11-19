package com.example.unsplash.data.mappers

import com.example.unsplash.data.local.PhotoEntity
import com.example.unsplash.data.remote.PhotoDto
import com.example.unsplash.domain.Photo

fun PhotoDto.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = this.id,
        author = this.author,
        width = this.width,
        height = this.height,
        url = this.url,
        downloadUrl = this.download_url
    )
}

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        id = this.id,
        author = this.author,
        width = this.width,
        height = this.height,
        url = this.url,
        downloadUrl = this.downloadUrl
    )
}