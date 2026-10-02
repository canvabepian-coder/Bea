package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PhotoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE folderId = :folderId ORDER BY timestamp DESC")
    fun getPhotosForFolder(folderId: Long): Flow<List<PhotoItem>>

    @Query("SELECT COUNT(*) FROM photos WHERE folderId = :folderId")
    suspend fun getPhotoCountForFolder(folderId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoItem): Long

    @Delete
    suspend fun deletePhoto(photo: PhotoItem)

    @Query("DELETE FROM photos WHERE folderId = :folderId")
    suspend fun deletePhotosForFolder(folderId: Long)
}
