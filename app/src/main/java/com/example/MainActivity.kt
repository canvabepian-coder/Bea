package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PhotoDetailDialog
import com.example.ui.screens.CaptureScreen
import com.example.ui.screens.GalleryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PhotoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: PhotoViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val projectNameInput by viewModel.projectNameInput.collectAsStateWithLifecycle()
    val activeFolder by viewModel.activeFolder.collectAsStateWithLifecycle()
    val activePhotos by viewModel.activePhotos.collectAsStateWithLifecycle()
    val allFolders by viewModel.allFolders.collectAsStateWithLifecycle()
    val selectedPhoto by viewModel.selectedPhotoForDetail.collectAsStateWithLifecycle()
    val feedbackMessage by viewModel.feedbackMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(feedbackMessage) {
        feedbackMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearFeedbackMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            label = "ScreenTransition"
        ) { screen ->
            when (screen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        projectName = projectNameInput,
                        onProjectNameChange = viewModel::onProjectNameChange,
                        onStartSession = { customName ->
                            viewModel.startPhotoSession(customName)
                        },
                        recentFolders = allFolders,
                        onSelectFolder = { folder ->
                            viewModel.selectExistingFolder(folder)
                        }
                    )
                }

                AppScreen.CAPTURE -> {
                    val folder = activeFolder
                    if (folder != null) {
                        CaptureScreen(
                            folder = folder,
                            photos = activePhotos,
                            onPrepareCaptureUri = {
                                viewModel.preparePhotoCaptureUri()
                            },
                            onPhotoCaptureResult = { success ->
                                viewModel.onPhotoCaptureResult(success)
                            },
                            onPhotoSelected = { photo ->
                                viewModel.selectPhotoForDetail(photo)
                            },
                            onFinishAndChangeProject = {
                                viewModel.finishAndChangeProject()
                            },
                            onOpenGallery = {
                                viewModel.openGallery()
                            }
                        )
                    } else {
                        // Fallback if no active folder
                        HomeScreen(
                            projectName = projectNameInput,
                            onProjectNameChange = viewModel::onProjectNameChange,
                            onStartSession = { customName ->
                                viewModel.startPhotoSession(customName)
                            },
                            recentFolders = allFolders,
                            onSelectFolder = { f ->
                                viewModel.selectExistingFolder(f)
                            }
                        )
                    }
                }

                AppScreen.GALLERY -> {
                    val folder = activeFolder
                    if (folder != null) {
                        GalleryScreen(
                            folder = folder,
                            photos = activePhotos,
                            onBackToCapture = {
                                viewModel.backToCapture()
                            },
                            onPhotoSelected = { photo ->
                                viewModel.selectPhotoForDetail(photo)
                            },
                            onDeleteFolder = { f ->
                                viewModel.deleteFolder(f)
                            }
                        )
                    }
                }
            }
        }

        // Photo inspection modal dialog
        selectedPhoto?.let { photo ->
            PhotoDetailDialog(
                photo = photo,
                onDismiss = { viewModel.selectPhotoForDetail(null) },
                onDelete = { photoToDelete ->
                    viewModel.deletePhoto(photoToDelete)
                }
            )
        }
    }
}
