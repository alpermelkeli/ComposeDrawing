package com.alpermelkeli.composedrawing

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Default implementation of [DrawController] that manages drawing state and operations.
 *
 * @param currentColor The initial color for drawing
 * @param initialStrokeWidth The initial stroke width (default: 10f)
 * @param eraserColor The color used for eraser strokes (default: White)
 */
class DrawControllerImpl(
    currentColor: Color,
    initialStrokeWidth: Float = 10f,
    private val eraserColor: Color = Color.White
) : DrawController {

    private val _paths = mutableStateListOf<DrawPath>()
    override val paths: List<DrawPath> = _paths

    private val _redoPaths = mutableStateListOf<DrawPath>()

    override val canUndo = mutableStateOf(false)
    override val canRedo = mutableStateOf(false)

    private val _currentStrokeWidth = mutableFloatStateOf(initialStrokeWidth)
    override val currentStrokeWidth get() = _currentStrokeWidth.floatValue

    private val _currentColor = mutableStateOf(currentColor)
    override val currentColor get() = _currentColor.value

    private val _currentPathEffect = mutableStateOf<PathEffect?>(null)
    override val currentPathEffect get() = _currentPathEffect.value

    private val _selectedTool = mutableStateOf(DrawTools.PENCIL1)
    override val selectedTool: DrawTools get() = _selectedTool.value

    private val _currentPath = mutableStateOf<Path?>(null)
    override val currentPath: Path? get() = _currentPath.value

    init {
        updateUndoRedoState()
    }

    override fun startDrawing(offset: Offset) {
        _currentPath.value = Path().apply {
            moveTo(offset.x, offset.y)
        }
    }

    override fun continueDrawing(offset: Offset) {
        _currentPath.value?.let { path ->
            path.lineTo(offset.x, offset.y)
            _currentPath.value = Path().apply {
                addPath(path)
            }
        }
    }

    override fun finishDrawing() {
        _currentPath.value?.let { path ->
            val drawPath = DrawPath(
                path = path,
                strokeWidth = currentStrokeWidth,
                brush = SolidColor(currentColor),
                color = currentColor,
                pathEffect = currentPathEffect,
                drawingTool = selectedTool
            )

            _paths.add(drawPath)
            _redoPaths.clear()
            updateUndoRedoState()
        }

        _currentPath.value = null
    }

    override fun undo() {
        if (_paths.isNotEmpty()) {
            val lastPath = _paths.removeLastOrNull()
            lastPath?.let { _redoPaths.add(it) }
            updateUndoRedoState()
        }
    }

    override fun redo() {
        if (_redoPaths.isNotEmpty()) {
            val redoPath = _redoPaths.removeLastOrNull()
            redoPath?.let { _paths.add(it) }
            updateUndoRedoState()
        }
    }

    override fun clear() {
        _paths.clear()
        _redoPaths.clear()
        _currentPath.value = null
        updateUndoRedoState()
    }

    override fun setPaintStrokeWidth(width: Float) {
        _currentStrokeWidth.floatValue = width
    }

    override fun setPathEffect(pathEffect: PathEffect?) {
        _currentPathEffect.value = pathEffect
    }

    override fun selectDrawingTool(drawingTool: DrawTools) {
        _selectedTool.value = drawingTool
    }

    override fun setColor(color: Color) {
        _currentColor.value = color
    }

    private fun updateUndoRedoState() {
        canUndo.value = _paths.isNotEmpty()
        canRedo.value = _redoPaths.isNotEmpty()
    }

    override fun drawPaths(drawScope: DrawScope) {
        with(drawScope) {
            _paths.forEach { drawPath ->
                if (drawPath.isErase()) {
                    // Use BlendMode.Clear for true erasing
                    drawPath(
                        path = drawPath.path,
                        color = Color.Transparent,
                        style = Stroke(
                            width = drawPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = drawPath.pathEffect
                        ),
                        blendMode = BlendMode.Clear
                    )
                } else {
                    val brush = SolidColor(
                        if (drawPath.drawingTool == DrawTools.PENCIL2)
                            drawPath.color.copy(alpha = 0.3f)
                        else
                            drawPath.color
                    )
                    drawPath(
                        path = drawPath.path,
                        brush = brush,
                        style = Stroke(
                            width = drawPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = drawPath.pathEffect
                        )
                    )
                }
            }

            _currentPath.value?.let { path ->
                if (selectedTool == DrawTools.ERASER) {
                    // Use BlendMode.Clear for true erasing
                    drawPath(
                        path = path,
                        color = Color.Transparent,
                        style = Stroke(
                            width = currentStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = currentPathEffect
                        ),
                        blendMode = BlendMode.Clear
                    )
                } else {
                    val brush = SolidColor(
                        if (selectedTool == DrawTools.PENCIL2)
                            currentColor.copy(alpha = 0.3f)
                        else
                            currentColor
                    )
                    drawPath(
                        path = path,
                        brush = brush,
                        style = Stroke(
                            width = currentStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = currentPathEffect
                        )
                    )
                }
            }
        }
    }
}
