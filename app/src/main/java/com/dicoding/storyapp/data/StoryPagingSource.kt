package com.dicoding.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.storyapp.network.ApiService
import com.dicoding.storyapp.network.response.DetailResponse

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, DetailResponse>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DetailResponse> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStory(token, page, params.loadSize)
            val listData = responseData.listStory

            LoadResult.Page(
                data = listData,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (listData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, DetailResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}