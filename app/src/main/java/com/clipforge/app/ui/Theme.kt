package com.clipforge.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Solid dark palette: very dark green surfaces, dark blue for the main action. No gradients anywhere.
val Bg = Color(0xFF06110C)           // app background (very dark green)
val Bg2 = Color(0xFF0B1A13)          // bars, sheets, dialogs
val CardBg = Color(0xFF11271D)       // raised surface (progress tracks, selected items)
val Panel = Color(0xFF0E211A)        // cards and buttons
val PanelStrong = Color(0xFF163226)  // selected tab, active chip, icon tiles
val Edge = Color(0xFF1F3D2E)         // 1dp borders
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA9BDB2)
val Accent = Color(0xFF4C8DF6)       // blue used for text, sliders, progress
val Accent2 = Color(0xFF4FD6A0)      // mint green used for success and tags
val ButtonBlue = Color(0xFF1A44A8)   // dark blue fill of the main buttons
val Danger = Color(0xFFFF6B6B)

/** A solid card: rounded, opaque, with a thin border. Cheap to draw while scrolling. */
fun Modifier.solidCard(radius: Dp, strong: Boolean = false): Modifier {
    val shape = RoundedCornerShape(radius)
    return this
        .clip(shape)
        .background(if (strong) PanelStrong else Panel)
        .border(1.dp, Edge, shape)
}

/** Plain solid background for a screen. */
fun Modifier.appBackdrop(): Modifier = this.background(Bg)

@Composable
fun ClipForgeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            onPrimary = Color.White,
            secondary = Accent2,
            secondaryContainer = PanelStrong,
            onSecondaryContainer = TextPrimary,
            background = Bg,
            onBackground = TextPrimary,
            surface = Bg2,
            onSurface = TextPrimary,
            surfaceVariant = CardBg,
            onSurfaceVariant = TextSecondary,
            error = Danger,
            outline = Edge,
            outlineVariant = Edge
        ),
        shapes = Shapes(
            small = RoundedCornerShape(14.dp),
            medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(28.dp)
        ),
        content = content
    )
}
