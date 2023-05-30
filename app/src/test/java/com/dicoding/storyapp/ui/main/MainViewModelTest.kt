package com.dicoding.storyapp.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.storyapp.DataDummy
import com.dicoding.storyapp.MainDispatcherRule
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.getOrAwaitValue
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.network.response.DetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var pref: UserPreference

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `When Data Not Null and Successfully Load Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data: PagingData<DetailResponse> = StoryPagingSource.snapshot(dummyStory)
        val expectedData = MutableLiveData<PagingData<DetailResponse>>()
        expectedData.value = data
        Mockito.`when`(storyRepository.getStory("")).thenReturn(expectedData)

        val mainViewModel = MainViewModel(pref, storyRepository)
        val actualData: PagingData<DetailResponse> = mainViewModel.story("").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = ItemAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)

        //Memastikan data tidak null
        Assert.assertNotNull(differ.snapshot())
        //Memastikan jumlah data sesuai dengan yang diharapkan
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        //Memastikan data pertama yang dikembalikan sesuai
        Assert.assertEquals(dummyStory[0].name, differ.snapshot()[0]?.name)
    }

    @Test
    fun `when Data Empty`() = runTest {
        val data: PagingData<DetailResponse> = PagingData.from(emptyList())
        val expectedData = MutableLiveData<PagingData<DetailResponse>>()
        expectedData.value = data
        Mockito.`when`(storyRepository.getStory("")).thenReturn(expectedData)
        val mainViewModel = MainViewModel(pref, storyRepository)
        val actualData: PagingData<DetailResponse> = mainViewModel.story("").getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = ItemAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualData)

        //Memastikan jumlah data yang dikembalikan nol
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<DetailResponse>>>() {
    companion object {
        fun snapshot(items: List<DetailResponse>): PagingData<DetailResponse> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<DetailResponse>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<DetailResponse>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}