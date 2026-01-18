package com.alpermelkeli.composedrawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged

/**
 * A composable that provides a drawing canvas with touch gesture support.
 *
 * @param modifier Modifier to apply to the canvas
 * @param controller The DrawController that manages drawing state
 * @param onDrawingStateChanged Callback invoked when drawing starts or ends (true = drawing, false = not drawing)
 * @param enabled Whether drawing is enabled on the canvas
 *
 * Example usage:
 * ```
 * val controller = rememberDrawController()
 *
 * DrawCanvas(
 *     modifier = Modifier.fillMaxSize(),
 *     controller = controller,
 *     onDrawingStateChanged = { isDrawing ->
 *         // Disable zoom when drawing
 *     }
 * )
 * ```
 */
@Composable
fun DrawCanvas(
    modifier: Modifier = Modifier,
    controller: DrawController,
    onDrawingStateChanged: ((Boolean) -> Unit)? = null,
    enabled: Boolean = true
) {
    Canvas(
        modifier = modifier
            // Required for BlendMode.Clear to work properly
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .then(
                if (enabled) {
                    Modifier.pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown()

                            var event = awaitPointerEvent()

                            // Ignore multi-touch gestures
                            if (event.changes.size > 1) {
                                return@awaitEachGesture
                            }

                            onDrawingStateChanged?.invoke(true)
                            val localPos = down.position
                            val x = localPos.x.coerceIn(0f, size.width.toFloat())
                            val y = localPos.y.coerceIn(0f, size.height.toFloat())
                            controller.startDrawing(Offset(x, y))
                            event.changes.forEach { it.consume() }

                            do {
                                event = awaitPointerEvent()

                                // Handle multi-touch interruption
                                if (event.changes.size > 1) {
                                    controller.finishDrawing()
                                    onDrawingStateChanged?.invoke(false)
                                    return@awaitEachGesture
                                }

                                val pointer = event.changes.find { it.id == down.id }

                                if (pointer != null && pointer.pressed) {
                                    if (pointer.positionChanged()) {
                                        val pointerPos = pointer.position
                                        val px = pointerPos.x.coerceIn(0f, size.width.toFloat())
                                        val py = pointerPos.y.coerceIn(0f, size.height.toFloat())
                                        controller.continueDrawing(Offset(px, py))
                                    }
                                    pointer.consume()
                                } else {
                                    break
                                }

                            } while (true)

                            controller.finishDrawing()
                            onDrawingStateChanged?.invoke(false)
                        }
                    }
                } else {
                    Modifier
                }
            )
    ) {
        controller.drawPaths(this)
    }
}

/**
 * Creates and remembers a [DrawController] instance.
 *
 * @param initialColor The initial drawing color (default: Black)
 * @param initialStrokeWidth The initial stroke width (default: 10f)
 * @param eraserColor The color used for eraser strokes (default: White)
 * @return A remembered [DrawController] instance
 *
 * Example usage:
 * ```
 * val controller = rememberDrawController(
 *     initialColor = Color.Blue,
 *     initialStrokeWidth = 8f
 * )
 * ```
 */
@Composable
fun rememberDrawController(
    initialColor: Color = Color.Black,
    initialStrokeWidth: Float = 10f,
    eraserColor: Color = Color.White
): DrawController {
    return remember {
        DrawControllerImpl(
            currentColor = initialColor,
            initialStrokeWidth = initialStrokeWidth,
            eraserColor = eraserColor
        )
    }
}
