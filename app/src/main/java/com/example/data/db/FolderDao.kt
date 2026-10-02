package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ProjectFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {
    @Query("SELECT * FROM project_folders ORDER BY updatedAt DESC")
    fun getAllFolders(): Flow<List<ProjectFolder>>

    @Query("SELECT * FROM project_folders WHERE id = :id LIMIT 1")
    suspend fun getFolderById(id: Long): ProjectFolder?

    @Query("SELECT * FROM project_folders WHERE LOWER(TRIM(name)) = LOWER(TRIM(:name)) LIMIT 1")
    suspend fun getFolderByName(name: String): ProjectFolder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: ProjectFolder): Long

    @Update
    suspend fun updateFolder(folder: ProjectFolder)

    @Delete
    suspend fun deleteFolder(folder: ProjectFolder)

    @Query("UPDATE project_folders SET photoCount = :count, lastPhotoPath = :lastPhoto, updatedAt = :updatedAt WHERE id = :folderId")
    suspend fun updateFolderStats(folderId: Long, count: Int, lastPhoto: String?, updatedAt: Long)
}
