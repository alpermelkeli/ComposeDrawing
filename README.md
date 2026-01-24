# Compose Drawing

[![](https://jitpack.io/v/alpermelkeli/compose-drawing.svg)](https://jitpack.io/#alpermelkeli/compose-drawing)

A Jetpack Compose library for drawing on canvas with customizable tools, undo/redo support, and flexible UI components.

<img width="410" height="857" alt="Ekran Resmi 2026-01-24 17 55 48" src="https://github.com/user-attachments/assets/bb61ba7f-788f-46d4-9ca6-d908e716e25f" />

<img width="410" height="857" alt="Ekran Resmi 2026-01-24 17 57 57" src="https://github.com/user-attachments/assets/2d8971fc-8325-47df-abae-1a6a59dbadcf" />

<img width="410" height="857" alt="Ekran Resmi 2026-01-24 17 56 10" src="https://github.com/user-attachments/assets/cdc9ff1e-3287-4eb6-884e-b90dab66d4f9" />

![Ekran Kaydı 2026-01-24 17 54 43](https://github.com/user-attachments/assets/dff71782-7737-4575-8f6e-20628579c36e)


## Features

- **DrawCanvas** - Touch-enabled drawing canvas with gesture support
- **DrawController** - State management for drawing operations
- **Multiple Tools** - Pencil, Highlighter, and Eraser (true erasing with BlendMode.Clear)
- **Undo/Redo** - Full history support
- **Color Picker** - Built-in color picker bottom sheet with color wheel and stroke settings
- **Customizable Menu** - Provide your own icons or use the built-in components
- **Multi-touch Support** - Properly handles pinch-to-zoom conflicts
- **Zoom Integration** - Works seamlessly with [Zoomable](https://github.com/mxalbert1996/Zoomable) library

## Installation

### Step 1: Add JitPack repository

Add the JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add the dependency

Add the dependency to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.alpermelkeli:compose-drawing:1.0.0")
}
```

### Optional: Add Zoomable for pinch-to-zoom support

For zoom + drawing integration, add the [Zoomable](https://github.com/usuiat/Zoomable) library:

```kotlin
dependencies {
    implementation("net.engawapg.lib:zoomable:2.8.1")
}
```

## Usage

### Basic Usage

```kotlin
@Composable
fun DrawingScreen() {
    val controller = rememberDrawController(
        initialColor = Color.Black,
        initialStrokeWidth = 8f
    )

    Box(modifier = Modifier.fillMaxSize()) {
        DrawCanvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            controller = controller
        )
    }
}
```

### With Drawing Menu

```kotlin
@Composable
fun DrawingScreen() {
    val controller = rememberDrawController()

    Box(modifier = Modifier.fillMaxSize()) {
        DrawCanvas(
            modifier = Modifier.fillMaxSize(),
            controller = controller
        )

        DrawControllerMenu(
            modifier = Modifier.align(Alignment.BottomCenter),
            controller = controller,
            icons = DrawMenuIcons(
                undoIcon = painterResource(R.drawable.ic_undo),
                redoIcon = painterResource(R.drawable.ic_redo),
                pencil1Icon = painterResource(R.drawable.ic_pencil),
                pencil2Icon = painterResource(R.drawable.ic_highlighter),
                eraserIcon = painterResource(R.drawable.ic_eraser),
                colorPickerIcon = painterResource(R.drawable.ic_palette)
            )
        )
    }
}
```

### With Material Icons

```kotlin
@Composable
fun DrawingScreen() {
    val controller = rememberDrawController()

    DrawControllerMenu(
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
}
```

### With Zoomable Integration

For pinch-to-zoom support while drawing, use the [Zoomable](https://github.com/usuiat/Zoomable) library:

```kotlin
@Composable
fun ZoomableDrawingScreen() {
    val controller = rememberDrawController()
    var isDrawing by remember { mutableStateOf(false) }

    val zoomState = rememberZoomState(maxScale = 4f)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(
                    zoomState = zoomState,
                    zoomEnabled = !isDrawing,  // Disable zoom while drawing
                    enableOneFingerZoom = false
                )
        ) {
            DrawCanvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                controller = controller,
                onDrawingStateChanged = { drawing ->
                    isDrawing = drawing
                }
            )
        }
    }
}
```

### Using Color Picker Bottom Sheet

The library includes a built-in color picker with:
- Color wheel for precise color selection
- Brightness slider
- Preset colors row
- Stroke width slider

```kotlin
@Composable
fun DrawingScreen() {
    val controller = rememberDrawController()
    var showColorPicker by remember { mutableStateOf(false) }

    // Open the color picker sheet
    if (showColorPicker) {
        DrawColorPickerSheet(
            controller = controller,
            onDismiss = { showColorPicker = false }
        )
    }
}
```

### Custom Color Picker

You can also provide your own color picker:

```kotlin
@Composable
fun DrawingScreen() {
    val controller = rememberDrawController()
    var showColorPicker by remember { mutableStateOf(false) }

    DrawControllerMenu(
        controller = controller,
        onOpenColorPicker = { showColorPicker = true },
        icons = DrawMenuIcons(/* ... */)
    )

    if (showColorPicker) {
        // Your custom color picker dialog
        MyColorPickerDialog(
            onColorSelected = { color ->
                controller.setColor(color)
                showColorPicker = false
            }
        )
    }
}
```

## API Reference

### DrawController

| Property/Method | Description |
|-----------------|-------------|
| `currentColor` | Current drawing color |
| `currentStrokeWidth` | Current stroke width |
| `selectedTool` | Currently selected tool (PENCIL1, PENCIL2, ERASER) |
| `canUndo` | Observable state for undo availability |
| `canRedo` | Observable state for redo availability |
| `setColor(color)` | Set drawing color |
| `setPaintStrokeWidth(width)` | Set stroke width |
| `selectDrawingTool(tool)` | Select drawing tool |
| `undo()` | Undo last action |
| `redo()` | Redo last undone action |
| `clear()` | Clear all drawings |

### DrawTools

| Tool | Description                   |
|------|-------------------------------|
| `PENCIL1` | Solid stroke pencil           |
| `PENCIL2` | Semi-transparent highlighter  |
| `ERASER` | Eraser (uses BlendMode.Clear) |

### DrawMenuIcons

| Property | Description |
|----------|-------------|
| `undoIcon` | Icon for undo button |
| `redoIcon` | Icon for redo button |
| `pencil1Icon` | Icon for primary pencil |
| `pencil2Icon` | Icon for highlighter |
| `eraserIcon` | Icon for eraser |
| `colorPickerIcon` | Icon for color picker |

## Related Libraries

- [Zoomable](https://github.com/usuiat/Zoomable) - For pinch-to-zoom functionality (`net.engawapg.lib:zoomable`)

## Sample App

Check out the `sample` module for a complete working example with zoom integration.

## License

```
Copyright 2024 Alper Melkeli

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
