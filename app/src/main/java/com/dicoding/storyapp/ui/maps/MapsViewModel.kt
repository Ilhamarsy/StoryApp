package com.dicoding.storyapp.ui.maps

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryWithLocation(token: String) = storyRepository.getStoryWithLocation("Bearer $token")
}