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
            // Drawing Canvas
            DrawCanvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                controller = controller,
                onDrawingStateChanged = { drawing ->
                    isDrawing = drawing
                }
            )

            // Tool Menu
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

            // Color palette row at the top
            ColorPaletteRow(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp),
                controller = controller
            )
        }
    }
}

@Composable
private fun ColorPaletteRow(
    modifier: Modifier = Modifier,
    controller: DrawController
) {
    val colors = listOf(
        Color.Black,
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Magenta,
        Color.Cyan,
        Color(0xFFFF9800) // Orange
    )

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            colors.forEach { color ->
                ColorButton(
                    color = color,
                    isSelected = controller.currentColor == color,
                    onClick = { controller.setColor(color) }
                )
            }
        }
    }
}

@Composable
private fun ColorButton(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(if (isSelected) 36.dp else 32.dp),
        color = color,
        shape = MaterialTheme.shapes.small,
        shadowElevation = if (isSelected) 4.dp else 0.dp,
        onClick = onClick
    ) {}
}
