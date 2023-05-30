package com.dicoding.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.database.StoryDatabase
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.network.ApiConfig

object Injection {
    fun provideStoryRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(database, apiService)
    }

    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository {
        val pref = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(pref, apiService)
    }
}