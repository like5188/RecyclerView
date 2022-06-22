package com.like.recyclerview.sample.paging3.data.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg objects: T)

    @Update
    suspend fun update(vararg objects: T)

    @Delete
    suspend fun delete(vararg objects: T)
}