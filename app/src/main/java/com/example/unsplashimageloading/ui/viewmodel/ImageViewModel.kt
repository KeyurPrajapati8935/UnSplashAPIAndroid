package com.example.unsplashimageloading.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unsplashimageloading.data.model.Image
import com.example.unsplashimageloading.data.repository.ImageRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(var imageRepository: ImageRepositoryImpl) : ViewModel() {

    private val _images = MutableLiveData<List<Image>>()
    val images: LiveData<List<Image>> = _images

    private val _error = MutableLiveData<String?>()

    fun loadImages(currentPage: Int, PER_PAGE: Int) {
        viewModelScope.launch {
            try {
                val newImages =  withContext(Dispatchers.IO){
                    imageRepository.getImages(
                        currentPage,
                        PER_PAGE
                    )
                }
                _images.postValue(newImages)
            } catch (e: Exception) {
                _error.value = "Error fetching images: ${e.message}"
            }
        }
    }
}
