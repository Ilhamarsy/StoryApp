package com.dicoding.storyapp.ui.login

import androidx.lifecycle.ViewModel
import com.dicoding.storyapp.data.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = userRepository.login(email, password)
}