package com.alpermelkeli.composedrawing.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.alpermelkeli.composedrawing.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingSampleScreen() {
    val controller = rememberDrawController(
        initialColor = Color.Black,
        initialStrokeWidth = 8f
    )

    var isDrawing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compose Drawing") },
                actions = {
                    IconButton(onClick = { controller.clear() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Drawing Canvas with white background
            DrawCanvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                controller = controller,
                onDrawingStateChanged = { drawing ->
                    isDrawing = drawing
                }
            )

            // Tool Menu with built-in color picker
            DrawControllerMenu(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                controller = controller,
                icons = DrawMenuIcons(
                    undoIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Undo),
                    redoIcon = rememberVectorPainter(Icons.AutoMirrored.Filled.Redo),
                    pencil1Icon = rememberVectorPainter(Icons.Default.Edit),
                    pencil2Icon = rememberVectorPainter(Icons.Default.Highlight),
                    eraserIcon = rememberVectorPainter(Icons.Default.CleaningServices),
                    colorPickerIcon = rememberVectorPainter(Icons.Default.Palette)
                )
            )

            // Drawing state indicator
            if (isDrawing) {
                Text(
                    text = "Drawing...",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
