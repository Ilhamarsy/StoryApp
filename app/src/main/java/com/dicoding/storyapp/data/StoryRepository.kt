package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.storyapp.database.StoryDatabase
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.response.DetailResponse
import com.dicoding.storyapp.network.response.StatResponse
import com.dicoding.storyapp.network.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService
) {
    fun upload(
        bearer: String,
        image: MultipartBody.Part,
        desc: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Result<StatResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStory(bearer, image, desc, lat, lon)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoryWithLocation(token: String): LiveData<Result<StoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoryWithLocation(token, 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStory(token: String): LiveData<PagingData<DetailResponse>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, "Bearer $token"),
            pagingSourceFactory = {
//                StoryPagingSource(apiService, "Bearer $token")
                storyDatabase.storyDao().getAllStory()

            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also { instance = it }
    }
}