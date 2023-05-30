package com.dicoding.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.storyapp.network.response.DetailResponse

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<DetailResponse>)

    @Query("SELECT * FROM story")
    fun getAllStory(): PagingSource<Int, DetailResponse>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}
