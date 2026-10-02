package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.db.AppDatabase
import com.example.data.model.PhotoItem
import com.example.data.model.ProjectFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoFolderRepository(
    private val context: Context,
    private val database: AppDatabase = AppDatabase.getInstance(context)
) {
    private val folderDao = database.folderDao()
    private val photoDao = database.photoDao()

    val allFolders: Flow<List<ProjectFolder>> = folderDao.getAllFolders()

    fun getPhotosForFolder(folderId: Long): Flow<List<PhotoItem>> {
        return photoDao.getPhotosForFolder(folderId)
    }

    suspend fun getOrCreateFolder(name: String): ProjectFolder = withContext(Dispatchers.IO) {
        val trimmedName = name.trim()
        val existing = folderDao.getFolderByName(trimmedName)
        if (existing != null) {
            val directory = File(existing.folderPath)
            if (!directory.exists()) {
                directory.mkdirs()
            }
            existing
        } else {
            val sanitized = sanitizeFolderName(trimmedName)
            val baseDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: context.filesDir
            val projectDir = File(baseDir, "FotoCarpetas/$sanitized")
            if (!projectDir.exists()) {
                projectDir.mkdirs()
            }

            val newFolder = ProjectFolder(
                name = trimmedName,
                sanitizedPathName = sanitized,
                folderPath = projectDir.absolutePath,
                photoCount = 0,
                lastPhotoPath = null,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            val id = folderDao.insertFolder(newFolder)
            newFolder.copy(id = id)
        }
    }

    suspend fun createNewPhotoFile(folder: ProjectFolder): Pair<File, Uri> = withContext(Dispatchers.IO) {
        val dir = File(folder.folderPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_${timeStamp}.jpg"
        val imageFile = File(dir, fileName)

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        Pair(imageFile, uri)
    }

    suspend fun registerCapturedPhoto(
        folder: ProjectFolder,
        photoFile: File
    ): PhotoItem = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date(now))
        val size = if (photoFile.exists()) photoFile.length() else 0L

        val photoItem = PhotoItem(
            folderId = folder.id,
            folderName = folder.name,
            fileName = photoFile.name,
            filePath = photoFile.absolutePath,
            fileSizeBytes = size,
            timestamp = now,
            formattedDateTime = formattedDate
        )

        val photoId = photoDao.insertPhoto(photoItem)
        val newCount = photoDao.getPhotoCountForFolder(folder.id)

        folderDao.updateFolderStats(
            folderId = folder.id,
            count = newCount,
            lastPhoto = photoFile.absolutePath,
            updatedAt = now
        )

        photoItem.copy(id = photoId)
    }

    suspend fun deletePhoto(photo: PhotoItem, folder: ProjectFolder) = withContext(Dispatchers.IO) {
        val file = File(photo.filePath)
        if (file.exists()) {
            file.delete()
        }
        photoDao.deletePhoto(photo)
        val newCount = photoDao.getPhotoCountForFolder(folder.id)
        folderDao.updateFolderStats(
            folderId = folder.id,
            count = newCount,
            lastPhoto = null,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteFolder(folder: ProjectFolder) = withContext(Dispatchers.IO) {
        val dir = File(folder.folderPath)
        if (dir.exists()) {
            dir.deleteRecursively()
        }
        photoDao.deletePhotosForFolder(folder.id)
        folderDao.deleteFolder(folder)
    }

    private fun sanitizeFolderName(name: String): String {
        val sanitized = name.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim()
        return if (sanitized.isEmpty()) "Proyecto_${System.currentTimeMillis()}" else sanitized
    }
}
