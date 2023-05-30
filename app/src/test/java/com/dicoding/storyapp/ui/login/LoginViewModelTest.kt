package com.dicoding.storyapp.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.network.response.LoginResponse
import com.dicoding.storyapp.data.Result
import com.dicoding.storyapp.getOrAwaitValue
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito

import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun `when login successful`(){
        val dummyData = DataDummy.generateDummyLoginResponse()
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Success(dummyData)
        Mockito.`when`(userRepository.login("email@gmail.com", "password")).thenReturn(expectedData)

        val viewModel = LoginViewModel(userRepository)
        val actualData = viewModel.login("email@gmail.com", "password").getOrAwaitValue()

        Assert.assertNotNull(actualData)
        Assert.assertEquals(dummyData.loginResult, (actualData as Result.Success).data.loginResult)
    }

    @Test
    fun `when login failed`(){
        val expectedData = MutableLiveData<Result<LoginResponse>>()
        expectedData.value = Result.Error("error")
        Mockito.`when`(userRepository.login("email@gmail.com", "password")).thenReturn(expectedData)

        val viewModel = LoginViewModel(userRepository)
        val actualData = viewModel.login("email@gmail.com", "password").getOrAwaitValue()

        Assert.assertNotNull(actualData)
        Assert.assertEquals(expectedData.value, actualData)
    }
}