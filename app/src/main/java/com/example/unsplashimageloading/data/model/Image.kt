package com.example.unsplashimageloading.data.model

import com.google.gson.annotations.SerializedName

data class Image(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("urls")
    val url: Urls? = null,
    @SerializedName("description")
    val description: String? = null
)

data class Urls(
    @SerializedName("regular")
    val regular: String? = null,
)