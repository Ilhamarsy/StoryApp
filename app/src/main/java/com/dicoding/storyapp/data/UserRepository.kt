package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.response.LoginResponse
import com.dicoding.storyapp.network.response.StatResponse
import retrofit2.HttpException
import java.io.IOException

class UserRepository(private val pref: UserPreference, private val apiService: ApiService) {
    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.postLogin(email, password)
            val loginResult = response.loginResult
            pref.saveUser(loginResult)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            emit(Result.Error("http"))
        } catch (e: IOException) {
            emit(Result.Error("con"))
        }
    }

    fun register(name: String, email: String, password: String): LiveData<Result<StatResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.postRegister(name, email, password)
                emit(Result.Success(response))
            } catch (e: Exception) {
                emit(Result.Error(e.message.toString()))
            }
        }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            pref: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(pref, apiService)
            }.also { instance = it }
    }
}