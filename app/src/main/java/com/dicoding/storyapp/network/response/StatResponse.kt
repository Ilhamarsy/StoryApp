package com.dicoding.storyapp.network.response

import com.google.gson.annotations.SerializedName

data class StatResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
