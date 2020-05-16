package com.joao.freshgiphy.api

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.joao.freshgiphy.models.Gif

@Dao
interface GifDao {
    @Query("SELECT * FROM gif")
    fun getAll(): LiveData<List<Gif>>

    @Insert
    fun insert(gif: Gif)

    @Delete
    fun delete(gif: Gif)
}