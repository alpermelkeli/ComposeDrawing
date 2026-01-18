# Compose Drawing

[![](https://jitpack.io/v/alpermelkeli/compose-drawing.svg)](https://jitpack.io/#alpermelkeli/compose-drawing)

A Jetpack Compose library for drawing on canvas with customizable tools, undo/redo support, and flexible UI components.

## Features

- **DrawCanvas** - Touch-enabled drawing canvas with gesture support
- **DrawController** - State management for drawing operations
- **Multiple Tools** - Pencil, Highlighter, and Eraser
- **Undo/Redo** - Full history support
- **Customizable Menu** - Provide your own icons or use the built-in color picker
- **Multi-touch Support** - Properly handles pinch-to-zoom conflicts

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

### Handling Drawing State (for Zoom Integration)

```kotlin
@Composable
fun ZoomableDrawingScreen() {
    val controller = rememberDrawController()
    var isDrawing by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zoomable(enabled = !isDrawing) // Disable zoom while drawing
    ) {
        DrawCanvas(
            modifier = Modifier.fillMaxSize(),
            controller = controller,
            onDrawingStateChanged = { drawing ->
                isDrawing = drawing
            }
        )
    }
}
```

### Custom Color Picker

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

| Tool | Description |
|------|-------------|
| `PENCIL1` | Solid stroke pencil |
| `PENCIL2` | Semi-transparent highlighter |
| `ERASER` | Eraser (draws with eraser color) |

### DrawMenuIcons

| Property | Description |
|----------|-------------|
| `undoIcon` | Icon for undo button |
| `redoIcon` | Icon for redo button |
| `pencil1Icon` | Icon for primary pencil |
| `pencil2Icon` | Icon for highlighter |
| `eraserIcon` | Icon for eraser |
| `colorPickerIcon` | Icon for color picker |

## Sample App

Check out the `sample` module for a complete working example.

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
