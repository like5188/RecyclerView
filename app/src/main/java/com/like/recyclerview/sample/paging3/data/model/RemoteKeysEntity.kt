package com.like.recyclerview.sample.paging3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RemoteKeysEntity(
    @PrimaryKey
    val remoteName: String,
    val nextKey: Int?
)