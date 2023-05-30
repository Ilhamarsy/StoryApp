package com.dicoding.storyapp.ui.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.network.response.DetailResponse
import com.dicoding.storyapp.network.response.LoginResult
import kotlinx.coroutines.launch

class MainViewModel(
    private val pref: UserPreference,
    private val storyRepository: StoryRepository
) : ViewModel() {

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun story(token: String): LiveData<PagingData<DetailResponse>> =
        storyRepository.getStory(token).cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}