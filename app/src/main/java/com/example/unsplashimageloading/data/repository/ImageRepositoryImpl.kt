package com.example.unsplashimageloading.data.repository

import com.example.unsplashimageloading.data.model.Image
import com.example.unsplashimageloading.data.model.Urls
import com.example.unsplashimageloading.network.UnsplashService

class ImageRepositoryImpl : ImageRepository {
    private val unsplashService = UnsplashService.create()
    override suspend fun getImages(
        page: Int,
        perPage: Int,
    ): List<Image> {
        val imageUrls = unsplashService.getImages(page, perPage)
            .mapNotNull { it.url?.regular }
        return imageUrls.map {
            Image(id = "", url = Urls(it), description = "")
        }
    }
}
