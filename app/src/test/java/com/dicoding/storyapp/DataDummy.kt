package com.dicoding.storyapp

import com.dicoding.storyapp.network.response.DetailResponse
import com.dicoding.storyapp.network.response.LoginResponse
import com.dicoding.storyapp.network.response.LoginResult
import com.dicoding.storyapp.network.response.StatResponse

object DataDummy {
    fun generateDummyRegisterResponse(): StatResponse {
        return StatResponse(false, "Register succesful")
    }

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(name = "name", userId = "userId", token = "token")
        return LoginResponse(loginResult, false, "Login succesful")
    }

    fun generateDummyStoryResponse(): List<DetailResponse> {
        val items: MutableList<DetailResponse> = arrayListOf()
        for (i in 0..100) {
            val story = DetailResponse(
                "",
                "author + $i",
                "story $i",
                "test",
                null,
                i.toString(),
                null
            )
            items.add(story)
        }
        return items
    }
}
