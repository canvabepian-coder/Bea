package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val folderId: Long,
    val folderName: String,
    val fileName: String,
    val filePath: String,
    val fileSizeBytes: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedDateTime: String
)
