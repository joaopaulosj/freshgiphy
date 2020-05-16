package com.joao.freshgiphy.api

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joao.freshgiphy.models.Gif

@Database(entities = [Gif::class], version = 1)
abstract class GifDatabase : RoomDatabase() {
    abstract fun userDao(): GifDao
}