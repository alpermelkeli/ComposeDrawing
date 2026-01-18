package com.alpermelkeli.composedrawing

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

/**
 * Configuration class for [DrawControllerMenu] icons.
 * All painters are optional - if not provided, that button won't be shown.
 *
 * @property undoIcon Icon for the undo button
 * @property redoIcon Icon for the redo button
 * @property pencil1Icon Icon for the primary pencil tool
 * @property pencil2Icon Icon for the secondary pencil/highlighter tool
 * @property eraserIcon Icon for the eraser tool
 * @property colorPickerIcon Icon for the color picker button
 */
data class DrawMenuIcons(
    val undoIcon: Painter? = null,
    val redoIcon: Painter? = null,
    val pencil1Icon: Painter? = null,
    val pencil2Icon: Painter? = null,
    val eraserIcon: Painter? = null,
    val colorPickerIcon: Painter? = null
)

/**
 * A composable menu for controlling drawing operations.
 *
 * @param modifier Modifier to apply to the menu
 * @param controller The DrawController to control
 * @param icons Configuration for menu icons
 * @param onOpenColorPicker Callback when color picker button is clicked. If null, built-in color picker is shown.
 * @param containerColor Background color of the menu card
 * @param selectedToolOffset Y offset in dp for selected tool indicator (default: -5)
 *
 * Example usage:
 * ```
 * DrawControllerMenu(
 *     modifier = Modifier.align(Alignment.BottomCenter),
 *     controller = controller,
 *     icons = DrawMenuIcons(
 *         undoIcon = painterResource(R.drawable.ic_undo),
 *         redoIcon = painterResource(R.drawable.ic_redo),
 *         pencil1Icon = painterResource(R.drawable.ic_pencil),
 *         eraserIcon = painterResource(R.drawable.ic_eraser),
 *         colorPickerIcon = painterResource(R.drawable.ic_palette)
 *     ),
 *     onOpenColorPicker = { showCustomColorPicker = true }
 * )
 * ```
 */
@Composable
fun DrawControllerMenu(
    modifier: Modifier = Modifier,
    controller: DrawController,
    icons: DrawMenuIcons = DrawMenuIcons(),
    onOpenColorPicker: (() -> Unit)? = null,
    containerColor: Color = Color.White,
    selectedToolOffset: Int = -5
) {
    var showBuiltInColorPicker by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .wrapContentWidth()
            .padding(7.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(20.dp))

            // Undo/Redo buttons
            if (icons.undoIcon != null || icons.redoIcon != null) {
                Column(verticalArrangement = Arrangement.Center) {
                    icons.redoIcon?.let { icon ->
                        Icon(
                            painter = icon,
                            contentDescription = "Redo",
                            modifier = Modifier.clickable {
                                controller.redo()
                            }
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                    icons.undoIcon?.let { icon ->
                        Icon(
                            painter = icon,
                            contentDescription = "Undo",
                            modifier = Modifier.clickable {
                                controller.undo()
                            }
                        )
                    }
                }
                Spacer(Modifier.width(22.dp))
            }

            // Pencil 1
            icons.pencil1Icon?.let { icon ->
                Image(
                    painter = icon,
                    contentDescription = "Pencil 1",
                    modifier = Modifier
                        .height(40.dp)
                        .wrapContentWidth()
                        .offset(y = if (controller.selectedTool == DrawTools.PENCIL1) selectedToolOffset.dp else 0.dp)
                        .clickable {
                            controller.selectDrawingTool(DrawTools.PENCIL1)
                        }
                )
                Spacer(Modifier.width(22.dp))
            }

            // Pencil 2 (Highlighter)
            icons.pencil2Icon?.let { icon ->
                Image(
                    painter = icon,
                    contentDescription = "Pencil 2 / Highlighter",
                    modifier = Modifier
                        .height(40.dp)
                        .wrapContentWidth()
                        .offset(y = if (controller.selectedTool == DrawTools.PENCIL2) selectedToolOffset.dp else 0.dp)
                        .clickable {
                            controller.selectDrawingTool(DrawTools.PENCIL2)
                        }
                )
                Spacer(Modifier.width(22.dp))
            }

            // Eraser
            icons.eraserIcon?.let { icon ->
                Image(
                    painter = icon,
                    contentDescription = "Eraser",
                    modifier = Modifier
                        .height(40.dp)
                        .wrapContentWidth()
                        .offset(y = if (controller.selectedTool == DrawTools.ERASER) selectedToolOffset.dp else 0.dp)
                        .clickable {
                            controller.selectDrawingTool(DrawTools.ERASER)
                        }
                )
                Spacer(Modifier.width(40.dp))
            }

            // Color picker
            icons.colorPickerIcon?.let { icon ->
                Image(
                    painter = icon,
                    contentDescription = "Color Picker",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            if (onOpenColorPicker != null) {
                                onOpenColorPicker()
                            } else {
                                showBuiltInColorPicker = true
                            }
                        }
                )
            }

            Spacer(Modifier.width(20.dp))
        }
    }

    // Built-in Color Picker Bottom Sheet
    if (showBuiltInColorPicker) {
        DrawColorPickerSheet(
            controller = controller,
            onDismiss = { showBuiltInColorPicker = false }
        )
    }
}

/**
 * A minimal menu with just undo/redo and clear functionality.
 * Use this when you want to provide your own tool selection UI.
 *
 * @param modifier Modifier to apply to the menu
 * @param controller The DrawController to control
 * @param undoIcon Icon for the undo button
 * @param redoIcon Icon for the redo button
 * @param clearIcon Icon for the clear button
 * @param containerColor Background color of the menu card
 */
@Composable
fun DrawControllerMinimalMenu(
    modifier: Modifier = Modifier,
    controller: DrawController,
    undoIcon: Painter? = null,
    redoIcon: Painter? = null,
    clearIcon: Painter? = null,
    containerColor: Color = Color.White
) {
    Card(
        modifier = modifier
            .wrapContentWidth()
            .padding(7.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            undoIcon?.let { icon ->
                IconButton(
                    onClick = { controller.undo() },
                    enabled = controller.canUndo.value
                ) {
                    Icon(painter = icon, contentDescription = "Undo")
                }
            }

            redoIcon?.let { icon ->
                IconButton(
                    onClick = { controller.redo() },
                    enabled = controller.canRedo.value
                ) {
                    Icon(painter = icon, contentDescription = "Redo")
                }
            }

            clearIcon?.let { icon ->
                IconButton(onClick = { controller.clear() }) {
                    Icon(painter = icon, contentDescription = "Clear")
                }
            }
        }
    }
}
