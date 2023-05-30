package com.dicoding.storyapp.network

import com.dicoding.storyapp.network.response.LoginResponse
import com.dicoding.storyapp.network.response.StatResponse
import com.dicoding.storyapp.network.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("stories")
    suspend fun getStory(
        @Header("Authorization") bearer: String,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): StoryResponse

    @GET("stories")
    suspend fun getStoryWithLocation(
        @Header("Authorization") bearer: String,
        @Query("location") location: Int
    ): StoryResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun postRegister(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): StatResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun postLogin(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): StatResponse
}