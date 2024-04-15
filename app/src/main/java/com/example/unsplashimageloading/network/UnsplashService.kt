package com.example.unsplashimageloading.network

import com.example.unsplashimageloading.BuildConfig
import com.example.unsplashimageloading.data.model.Image
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface UnsplashService {

    @Headers("Authorization: Client-ID " + BuildConfig.API_KEY)
    @GET("photos")
    suspend fun getImages(
        @Query("page") pageNo: Int,
        @Query("per_page") perPage: Int,
    ): List<Image>

    companion object {
        fun create(): UnsplashService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(UnsplashService::class.java)
        }

        var BASE_URL = "https://api.unsplash.com/"
    }
}