package com.dicoding.storyapp.ui.register

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.data.UserRepository
import com.dicoding.storyapp.network.response.StatResponse
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
class RegisterViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun `when Register successful`() {
        val dummyData = DataDummy.generateDummyRegisterResponse()
        val expectedData = MutableLiveData<Result<StatResponse>>()
        expectedData.value = Result.Success(dummyData)
        Mockito.`when`(userRepository.register("name", "email@gmail.com", "password"))
            .thenReturn(expectedData)

        val viewModel = RegisterViewModel(userRepository)
        val actualData = viewModel.register("name", "email@gmail.com", "password").getOrAwaitValue()

        Assert.assertNotNull(actualData)
        Assert.assertEquals(dummyData, (actualData as Result.Success).data)
    }

    @Test
    fun `when Register failed`() {
        val expectedData = MutableLiveData<Result<StatResponse>>()
        expectedData.value = Result.Error("error")
        Mockito.`when`(userRepository.register("name", "email@gmail.com", "password"))
            .thenReturn(expectedData)

        val viewModel = RegisterViewModel(userRepository)
        val actualData = viewModel.register("name", "email@gmail.com", "password").getOrAwaitValue()

        Assert.assertNotNull(actualData)
        Assert.assertEquals(expectedData.value, actualData)
    }
}