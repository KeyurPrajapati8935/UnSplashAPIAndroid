package com.example.unsplashimageloading.data.repository

import com.example.unsplashimageloading.data.model.Image

interface ImageRepository {
    suspend fun getImages(
        page: Int,
        perPage: Int,
    ): List<Image>
}