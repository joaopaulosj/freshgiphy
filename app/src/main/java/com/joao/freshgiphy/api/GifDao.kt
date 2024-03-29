package com.joao.freshgiphy.api

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.joao.freshgiphy.models.Gif
import io.reactivex.Single

@Dao
interface GifDao {
    @Query("SELECT * FROM gif ORDER BY dbId DESC")
    fun getAll(): Single<List<Gif>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(gif: Gif)

    @Query("DELETE FROM gif WHERE id = :id")
    fun delete(id: String)
}