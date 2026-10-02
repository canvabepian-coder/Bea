package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.PhotoItem
import com.example.data.model.ProjectFolder
import com.example.data.repository.PhotoFolderRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

enum class AppScreen {
    HOME,
    CAPTURE,
    GALLERY
}

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PhotoFolderRepository(application)

    val allFolders: StateFlow<List<ProjectFolder>> = repository.allFolders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _projectNameInput = MutableStateFlow("")
    val projectNameInput: StateFlow<String> = _projectNameInput.asStateFlow()

    private val _activeFolder = MutableStateFlow<ProjectFolder?>(null)
    val activeFolder: StateFlow<ProjectFolder?> = _activeFolder.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activePhotos: StateFlow<List<PhotoItem>> = _activeFolder.flatMapLatest { folder ->
        if (folder != null) {
            repository.getPhotosForFolder(folder.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedPhotoForDetail = MutableStateFlow<PhotoItem?>(null)
    val selectedPhotoForDetail: StateFlow<PhotoItem?> = _selectedPhotoForDetail.asStateFlow()

    private var pendingPhotoFile: File? = null

    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    fun onProjectNameChange(name: String) {
        _projectNameInput.value = name
    }

    fun startPhotoSession(customName: String? = null) {
        val targetName = (customName ?: _projectNameInput.value).trim()
        if (targetName.isBlank()) {
            _feedbackMessage.value = "Por favor escribe un nombre para la carpeta o proyecto"
            return
        }

        viewModelScope.launch {
            val folder = repository.getOrCreateFolder(targetName)
            _activeFolder.value = folder
            _currentScreen.value = AppScreen.CAPTURE
        }
    }

    fun selectExistingFolder(folder: ProjectFolder) {
        _projectNameInput.value = folder.name
        _activeFolder.value = folder
        _currentScreen.value = AppScreen.CAPTURE
    }

    suspend fun preparePhotoCaptureUri(): Uri? {
        val currentFolder = _activeFolder.value ?: return null
        val (file, uri) = repository.createNewPhotoFile(currentFolder)
        pendingPhotoFile = file
        return uri
    }

    fun onPhotoCaptureResult(success: Boolean) {
        val file = pendingPhotoFile
        val folder = _activeFolder.value
        if (success && file != null && folder != null && file.exists() && file.length() > 0) {
            viewModelScope.launch {
                val photo = repository.registerCapturedPhoto(folder, file)
                // Refresh folder reference
                _activeFolder.value = folder.copy(
                    photoCount = folder.photoCount + 1,
                    lastPhotoPath = photo.filePath,
                    updatedAt = System.currentTimeMillis()
                )
                _feedbackMessage.value = "Foto guardada en ${folder.name}"
            }
        } else {
            // Clean up empty file if cancelled
            if (file != null && file.exists() && file.length() == 0L) {
                file.delete()
            }
        }
        pendingPhotoFile = null
    }

    fun finishAndChangeProject() {
        _activeFolder.value = null
        _projectNameInput.value = ""
        _currentScreen.value = AppScreen.HOME
    }

    fun openGallery() {
        _currentScreen.value = AppScreen.GALLERY
    }

    fun backToCapture() {
        _currentScreen.value = AppScreen.CAPTURE
    }

    fun selectPhotoForDetail(photo: PhotoItem?) {
        _selectedPhotoForDetail.value = photo
    }

    fun deletePhoto(photo: PhotoItem) {
        val folder = _activeFolder.value ?: return
        viewModelScope.launch {
            repository.deletePhoto(photo, folder)
            if (_selectedPhotoForDetail.value?.id == photo.id) {
                _selectedPhotoForDetail.value = null
            }
            _activeFolder.value = folder.copy(
                photoCount = (folder.photoCount - 1).coerceAtLeast(0)
            )
            _feedbackMessage.value = "Foto eliminada"
        }
    }

    fun deleteFolder(folder: ProjectFolder) {
        viewModelScope.launch {
            repository.deleteFolder(folder)
            if (_activeFolder.value?.id == folder.id) {
                finishAndChangeProject()
            }
            _feedbackMessage.value = "Carpeta eliminada"
        }
    }

    fun clearFeedbackMessage() {
        _feedbackMessage.value = null
    }
}
