package com.dicoding.storyapp.ui.addstory

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun upload(
        bearer: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = storyRepository.upload("Bearer $bearer", image, desc, lat, lon)
}