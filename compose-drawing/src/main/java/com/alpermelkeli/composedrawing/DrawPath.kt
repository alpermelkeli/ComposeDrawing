package com.alpermelkeli.composedrawing

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor

/**
 * Data class representing a drawn path with its visual properties.
 *
 * @property path The geometric path data
 * @property brush The brush used to draw the path
 * @property strokeWidth The width of the stroke
 * @property color The color of the stroke
 * @property pathEffect Optional path effect (dashed, dotted, etc.)
 * @property drawingTool The tool used to create this path
 */
data class DrawPath(
    val path: Path,
    val brush: SolidColor,
    val strokeWidth: Float,
    val color: Color,
    val pathEffect: PathEffect? = null,
    val drawingTool: DrawTools = DrawTools.PENCIL1
) {
    /**
     * Returns true if this path was drawn with the eraser tool.
     */
    fun isErase(): Boolean {
        return drawingTool == DrawTools.ERASER
    }
}
