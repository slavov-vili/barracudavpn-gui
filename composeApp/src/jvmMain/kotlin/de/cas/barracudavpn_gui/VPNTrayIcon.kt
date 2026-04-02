package de.cas.barracudavpn_gui

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Tray

@Composable
fun VPNTrayIcon(app: ApplicationScope, onExit: () -> Unit) {
    app.Tray(
        icon = TrayIcon,
        menu = {
            Item(
                text = "Configure",
                onClick = { print("hello") })
            Item(
                text = "Exit",
                onClick = onExit
            )
        })
}

object TrayIcon : Painter() {
    override val intrinsicSize = Size(256f, 256f)

    override fun DrawScope.onDraw() {
        drawOval(Color(0xFFFFA500))
    }
}