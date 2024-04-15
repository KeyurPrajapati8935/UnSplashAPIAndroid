package com.example.unsplashimageloading.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.unsplashimageloading.data.repository.ImageRepositoryImpl

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            return ImageViewModel(ImageRepositoryImpl()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}