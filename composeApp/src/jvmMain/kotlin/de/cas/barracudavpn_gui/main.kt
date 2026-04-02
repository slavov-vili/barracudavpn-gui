package de.cas.barracudavpn_gui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.isTraySupported
import androidx.compose.ui.window.rememberWindowState


fun main() = application {
    val viewModel = remember { VPNViewModel() }
    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize(600.dp, 800.dp)
    )

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF5577D1),
            onPrimary = Color.Black,
            secondary = Color.Gray,
            outline = Color(0xFF2A3F74),
            surface = Color(0xFFFFCCE3),
        )
    ) {
        VPNWindow(
            viewModel = viewModel,
            windowState = windowState,
            onCloseRequest = ::exitApplication
        )

        if (isTraySupported) {
            VPNTrayIcon(this, onExit = ::exitApplication)
        }

    }
}
