package com.alpermelkeli.composedrawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

/**
 * A bottom sheet for picking colors and adjusting stroke settings.
 *
 * @param controller The DrawController to update
 * @param onDismiss Callback when the sheet is dismissed
 * @param title Title shown at the top of the sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawColorPickerSheet(
    controller: DrawController,
    onDismiss: () -> Unit,
    title: String = "Drawing Settings"
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        DrawColorPickerContent(
            controller = controller,
            title = title,
            onDone = onDismiss
        )
    }
}

/**
 * Content for the color picker, can be used standalone without the bottom sheet.
 */
@Composable
fun DrawColorPickerContent(
    controller: DrawController,
    title: String = "Drawing Settings",
    onDone: (() -> Unit)? = null
) {
    var selectedColor by remember { mutableStateOf(controller.currentColor) }
    var strokeWidth by remember { mutableFloatStateOf(controller.currentStrokeWidth) }
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var brightness by remember { mutableFloatStateOf(1f) }

    // Initialize HSB from current color
    LaunchedEffect(Unit) {
        val hsb = colorToHSB(controller.currentColor)
        hue = hsb[0]
        saturation = hsb[1]
        brightness = hsb[2]
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Preview
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color preview circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )

            Spacer(modifier = Modifier.width(24.dp))

            // Stroke preview
            Canvas(
                modifier = Modifier
                    .width(150.dp)
                    .height(60.dp)
            ) {
                val centerY = size.height / 2
                drawLine(
                    color = selectedColor,
                    start = Offset(20f, centerY),
                    end = Offset(size.width - 20f, centerY),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Color wheel
        ColorWheel(
            modifier = Modifier.size(200.dp),
            hue = hue,
            saturation = saturation,
            onColorSelected = { h, s ->
                hue = h
                saturation = s
                selectedColor = hsbToColor(hue, saturation, brightness)
                controller.setColor(selectedColor)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Brightness slider
        Text(
            text = "Brightness",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )
        Slider(
            value = brightness,
            onValueChange = {
                brightness = it
                selectedColor = hsbToColor(hue, saturation, brightness)
                controller.setColor(selectedColor)
            },
            valueRange = 0f..1f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = selectedColor,
                activeTrackColor = selectedColor
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Preset colors
        Text(
            text = "Preset Colors",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PresetColorRow(
            selectedColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
                val hsb = colorToHSB(color)
                hue = hsb[0]
                saturation = hsb[1]
                brightness = hsb[2]
                controller.setColor(color)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stroke width slider
        Text(
            text = "Stroke Width: ${strokeWidth.toInt()}",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.Start)
        )
        Slider(
            value = strokeWidth,
            onValueChange = {
                strokeWidth = it
                controller.setPaintStrokeWidth(it)
            },
            valueRange = 1f..50f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = selectedColor,
                activeTrackColor = selectedColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Done button
        if (onDone != null) {
            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = selectedColor
                )
            ) {
                Text(
                    text = "Done",
                    color = if (brightness > 0.5f) Color.Black else Color.White
                )
            }
        }
    }
}

@Composable
private fun ColorWheel(
    modifier: Modifier = Modifier,
    hue: Float,
    saturation: Float,
    onColorSelected: (hue: Float, saturation: Float) -> Unit
) {
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableFloatStateOf(0f) }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val (h, s) = offsetToHueSaturation(offset, center, radius)
                    if (s <= 1f) {
                        onColorSelected(h, s.coerceIn(0f, 1f))
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val (h, s) = offsetToHueSaturation(change.position, center, radius)
                    if (s <= 1.1f) {
                        onColorSelected(h, s.coerceIn(0f, 1f))
                    }
                }
            }
    ) {
        center = Offset(size.width / 2, size.height / 2)
        radius = minOf(size.width, size.height) / 2

        // Draw color wheel
        for (angle in 0 until 360) {
            val startAngle = angle.toFloat()
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        hsbToColor(startAngle, 1f, 1f)
                    ),
                    center = center,
                    radius = radius
                ),
                startAngle = startAngle,
                sweepAngle = 1.5f,
                useCenter = true
            )
        }

        // Draw selection indicator
        val selectorAngle = Math.toRadians(hue.toDouble())
        val selectorRadius = saturation * radius
        val selectorX = center.x + (selectorRadius * cos(selectorAngle)).toFloat()
        val selectorY = center.y + (selectorRadius * sin(selectorAngle)).toFloat()

        drawCircle(
            color = Color.White,
            radius = 12.dp.toPx(),
            center = Offset(selectorX, selectorY),
            style = Stroke(width = 3.dp.toPx())
        )
        drawCircle(
            color = Color.Black,
            radius = 10.dp.toPx(),
            center = Offset(selectorX, selectorY),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun PresetColorRow(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    val presetColors = listOf(
        Color.Black,
        Color.White,
        Color.Red,
        Color(0xFFFF9800), // Orange
        Color.Yellow,
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFF9C27B0), // Purple
        Color(0xFFE91E63), // Pink
        Color(0xFF795548), // Brown
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        presetColors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = if (selectedColor == color) 3.dp else 1.dp,
                        color = if (selectedColor == color)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}

private fun offsetToHueSaturation(offset: Offset, center: Offset, radius: Float): Pair<Float, Float> {
    val dx = offset.x - center.x
    val dy = offset.y - center.y
    val distance = sqrt(dx * dx + dy * dy)
    val saturation = distance / radius

    var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    if (angle < 0) angle += 360f

    return Pair(angle, saturation)
}

private fun hsbToColor(hue: Float, saturation: Float, brightness: Float): Color {
    val h = (hue % 360) / 60f
    val s = saturation.coerceIn(0f, 1f)
    val v = brightness.coerceIn(0f, 1f)

    val i = h.toInt()
    val f = h - i
    val p = v * (1 - s)
    val q = v * (1 - s * f)
    val t = v * (1 - s * (1 - f))

    return when (i % 6) {
        0 -> Color(v, t, p)
        1 -> Color(q, v, p)
        2 -> Color(p, v, t)
        3 -> Color(p, q, v)
        4 -> Color(t, p, v)
        else -> Color(v, p, q)
    }
}

private fun colorToHSB(color: Color): FloatArray {
    val r = color.red
    val g = color.green
    val b = color.blue

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val brightness = max

    val saturation = if (max == 0f) 0f else delta / max

    val hue = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6)
        max == g -> 60f * (((b - r) / delta) + 2)
        else -> 60f * (((r - g) / delta) + 4)
    }.let { if (it < 0) it + 360 else it }

    return floatArrayOf(hue, saturation, brightness)
}
