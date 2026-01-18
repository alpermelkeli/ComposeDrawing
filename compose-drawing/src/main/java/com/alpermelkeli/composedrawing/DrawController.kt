package com.alpermelkeli.composedrawing

import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Interface defining the contract for a drawing controller.
 * Manages drawing state, paths, and operations.
 *
 * Use [rememberDrawController] to create an instance in a Composable.
 */
interface DrawController {
    /** Current stroke width for drawing */
    val currentStrokeWidth: Float

    /** Current drawing color */
    val currentColor: Color

    /** Current path effect (dashed, dotted, etc.) */
    val currentPathEffect: PathEffect?

    /** Currently selected drawing tool */
    val selectedTool: DrawTools

    /** Observable state indicating if undo is available */
    val canUndo: MutableState<Boolean>

    /** Observable state indicating if redo is available */
    val canRedo: MutableState<Boolean>

    /** List of completed drawing paths */
    val paths: List<DrawPath>

    /** The path currently being drawn (null if not drawing) */
    val currentPath: Path?

    /**
     * Start drawing at the given offset.
     * @param offset The starting point of the stroke
     */
    fun startDrawing(offset: Offset)

    /**
     * Continue drawing to the given offset.
     * @param offset The next point in the stroke
     */
    fun continueDrawing(offset: Offset)

    /**
     * Finish the current drawing stroke.
     */
    fun finishDrawing()

    /**
     * Undo the last drawing action.
     */
    fun undo()

    /**
     * Redo a previously undone action.
     */
    fun redo()

    /**
     * Clear all drawn paths.
     */
    fun clear()

    /**
     * Set the stroke width for subsequent drawings.
     * @param width The stroke width in pixels
     */
    fun setPaintStrokeWidth(width: Float)

    /**
     * Set a path effect for subsequent drawings.
     * @param pathEffect The path effect to apply, or null for solid lines
     */
    fun setPathEffect(pathEffect: PathEffect?)

    /**
     * Select a drawing tool.
     * @param drawingTool The tool to select
     */
    fun selectDrawingTool(drawingTool: DrawTools)

    /**
     * Set the drawing color.
     * @param color The color to use for subsequent drawings
     */
    fun setColor(color: Color)

    /**
     * Draw all paths onto the given DrawScope.
     * Call this from a Canvas composable.
     * @param drawScope The DrawScope to draw onto
     */
    fun drawPaths(drawScope: DrawScope)
}
