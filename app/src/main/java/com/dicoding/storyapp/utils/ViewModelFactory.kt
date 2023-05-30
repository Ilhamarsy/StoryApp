package com.dicoding.storyapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.di.Injection
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.ui.addstory.AddStoryViewModel
import com.dicoding.storyapp.ui.login.LoginViewModel
import com.dicoding.storyapp.ui.main.MainViewModel
import com.dicoding.storyapp.ui.maps.MapsViewModel
import com.dicoding.storyapp.ui.register.RegisterViewModel

class ViewModelFactory(
    private val dataStore: DataStore<Preferences>,
    private val context: Context
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    UserPreference.getInstance(dataStore),
                    Injection.provideStoryRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideUserRepository(dataStore)) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(Injection.provideUserRepository(dataStore)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(Injection.provideStoryRepository(context)) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(Injection.provideStoryRepository(context)) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}