package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProjectFolder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    projectName: String,
    onProjectNameChange: (String) -> Unit,
    onStartSession: (String?) -> Unit,
    recentFolders: List<ProjectFolder>,
    onSelectFolder: (ProjectFolder) -> Unit
) {
    val exampleSuggestions = listOf(
        "Cliente López",
        "Mantenimiento 1",
        "Recibos de Obra",
        "Inspección Técnica"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F9FA) // Fondo claro y limpio
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(28.dp))

                // App Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF2E7D32)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "FotoCarpetas",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = "Organiza tus fotos de trabajo en segundos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF555555)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Main Central Card with Large Text Field & Start Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Title / Label requirement: "Nombre del Proyecto o Carpeta"
                        Text(
                            text = "Nombre del Proyecto o Carpeta",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E211F)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "(ejemplo: Cliente López, Mantenimiento 1, Recibos de Obra)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF6B7280)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Large central text field
                        OutlinedTextField(
                            value = projectName,
                            onValueChange = onProjectNameChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("project_name_input"),
                            placeholder = {
                                Text(
                                    text = "Escribe aquí el nombre...",
                                    fontSize = 18.sp,
                                    color = Color(0xFF9E9E9E)
                                )
                            },
                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B5E20)
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FolderOpen,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            trailingIcon = {
                                if (projectName.isNotEmpty()) {
                                    IconButton(
                                        onClick = { onProjectNameChange("") },
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Limpiar texto",
                                            tint = Color(0xFF757575)
                                        )
                                    }
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2E7D32),
                                unfocusedBorderColor = Color(0xFFD0D7DE),
                                focusedContainerColor = Color(0xFFF1F8E9),
                                unfocusedContainerColor = Color(0xFFFAFAFA)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick suggestion chips
                        Text(
                            text = "Sugerencias rápidas para tocar:",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF757575),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            exampleSuggestions.forEach { example ->
                                SuggestionChip(
                                    onClick = { onProjectNameChange(example) },
                                    label = {
                                        Text(
                                            text = example,
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 13.sp
                                        )
                                    },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = Color(0xFFE8F5E9),
                                        labelColor = Color(0xFF1B5E20)
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large Green Button: "📁 Iniciar Sesión de Fotos"
                        Button(
                            onClick = { onStartSession(null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .testTag("start_photo_session_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "📁",
                                    fontSize = 24.sp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Iniciar Sesión de Fotos",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Recent Folders section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "📁 Carpetas Creadas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E211F)
                    )
                    if (recentFolders.isNotEmpty()) {
                        Text(
                            text = "${recentFolders.size} proyectos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF757575)
                        )
                    }
                }
            }

            if (recentFolders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Aún no tienes carpetas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF333333)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Escribe el nombre del trabajo arriba y presiona el botón verde para crear la primera carpeta y tomar fotos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF757575),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(recentFolders, key = { it.id }) { folder ->
                    RecentFolderCard(
                        folder = folder,
                        onClick = { onSelectFolder(folder) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RecentFolderCard(
    folder: ProjectFolder,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("recent_folder_${folder.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail or folder icon
            if (folder.lastPhotoPath != null && File(folder.lastPhotoPath).exists()) {
                AsyncImage(
                    model = File(folder.lastPhotoPath),
                    contentDescription = "Última foto de ${folder.name}",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E211F)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (folder.photoCount == 1) "1 foto" else "${folder.photoCount} fotos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2E7D32)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(folder.updatedAt),
                        fontSize = 12.sp,
                        color = Color(0xFF757575)
                    )
                }
            }

            // Arrow action button
            Surface(
                shape = CircleShape,
                color = Color(0xFFF1F8E9),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "▶",
                        fontSize = 14.sp,
                        color = Color(0xFF2E7D32)
                    )
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
}
