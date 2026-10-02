package com.example.ui.screens

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PhotoItem
import com.example.data.model.ProjectFolder
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CaptureScreen(
    folder: ProjectFolder,
    photos: List<PhotoItem>,
    onPrepareCaptureUri: suspend () -> Uri?,
    onPhotoCaptureResult: (Boolean) -> Unit,
    onPhotoSelected: (PhotoItem) -> Unit,
    onFinishAndChangeProject: () -> Unit,
    onOpenGallery: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Handle back button to return to home project selector
    BackHandler {
        onFinishAndChangeProject()
    }

    // Scroll to the newest photo when taken
    LaunchedEffect(photos.size) {
        if (photos.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    // Camera capture contract
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        onPhotoCaptureResult(success)
    }

    fun launchCamera() {
        performHapticVibration(context)
        coroutineScope.launch {
            val uri = onPrepareCaptureUri()
            if (uri != null) {
                takePictureLauncher.launch(uri)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA) // Limpio y claro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ---------------------------------------------------------
            // PARTE SUPERIOR: Nombre del proyecto activo y Contador grande
            // ---------------------------------------------------------
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Card header con nombre del proyecto
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFE8F5E9)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Carpeta Activa",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                                Text(
                                    text = folder.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E211F),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Optional button to view full grid
                            if (photos.isNotEmpty()) {
                                FilledTonalButton(
                                    onClick = onOpenGallery,
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GridView,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Ver Todo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // CONTADOR GRANDE REQUERIDO: "Fotos tomadas en esta carpeta: X"
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF1F8E9),
                            border = BorderStroke(1.5.dp, Color(0xFFC8E6C9))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Fotos tomadas en esta carpeta:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "Guardado con fecha y hora exacta",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B6354)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${photos.size}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ---------------------------------------------------------
            // CENTRO: Botón redondo muy grande con icono de cámara: "📷 Tomar Foto"
            // ---------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Giant Round Camera Button
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            ambientColor = Color(0xFF2E7D32),
                            spotColor = Color(0xFF1B5E20)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF43A047),
                                    Color(0xFF2E7D32),
                                    Color(0xFF1B5E20)
                                )
                            )
                        )
                        .border(4.dp, Color.White, CircleShape)
                        .clickable(onClick = { launchCamera() })
                        .testTag("big_take_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Tomar Foto",
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TOMAR FOTO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "📷 Toca el botón verde para capturar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )

                Text(
                    text = "Se añadirá fecha y hora exacta automáticamente",
                    fontSize = 13.sp,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }

            // ---------------------------------------------------------
            // PARTE INFERIOR: Tira de miniaturas de fotos tomadas
            // ---------------------------------------------------------
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fotos en esta sesión (${photos.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E211F)
                    )

                    if (photos.isNotEmpty()) {
                        Text(
                            text = "Toca para ver o compartir",
                            fontSize = 12.sp,
                            color = Color(0xFF757575)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (photos.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = Color(0xFFBDBDBD),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Aún no hay fotos en esta carpeta.\nPresiona el botón de cámara para empezar.",
                                fontSize = 13.sp,
                                color = Color(0xFF757575),
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        itemsIndexed(photos, key = { _, item -> item.id }) { index, photo ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInHorizontally()
                            ) {
                                ThumbnailItem(
                                    photo = photo,
                                    index = photos.size - index,
                                    onClick = { onPhotoSelected(photo) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ---------------------------------------------------------
                // BOTÓN INFERIOR: "Finalizar y Cambiar de Proyecto"
                // ---------------------------------------------------------
                Button(
                    onClick = onFinishAndChangeProject,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .testTag("finish_change_project_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF37474F)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Finalizar y Cambiar de Proyecto",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun ThumbnailItem(
    photo: PhotoItem,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(92.dp)
            .clickable(onClick = onClick)
            .testTag("thumbnail_photo_${photo.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = File(photo.filePath),
                contentDescription = "Miniatura ${photo.formattedDateTime}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Index badge top-left
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xCC000000))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "#$index",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Timestamp overlay bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xB3000000))
                    .padding(vertical = 2.dp, horizontal = 4.dp)
                    .align(Alignment.BottomCenter)
            ) {
                val timeOnly = photo.formattedDateTime.split(" ").getOrNull(1) ?: photo.formattedDateTime
                Text(
                    text = timeOnly,
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun performHapticVibration(context: Context) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator?.vibrate(
                VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            )
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    } catch (_: Exception) {
    }
}
