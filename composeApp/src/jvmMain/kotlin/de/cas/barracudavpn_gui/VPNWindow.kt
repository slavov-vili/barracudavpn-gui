package de.cas.barracudavpn_gui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.border
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import java.awt.Cursor

// TODO: add icon somewhere which opens a configuration page
// TODO: configuration page has 2 tabs: 1 for the GUI and 1 for the vpn itself...
@Composable
fun VPNWindow(
    viewModel: VPNViewModel,
    windowState: WindowState,
    onCloseRequest: () -> Unit
) {
    val vpnState by viewModel.getStateFlow().collectAsState()

    Window(
        title = "BarracudaVPN-GUI",
        icon = TrayIcon,
        state = windowState,
        onCloseRequest = onCloseRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            val loginFieldStates = LoginFieldStates.empty()

            SelectionContainer(
                modifier = Modifier.weight(1f)
                    .fillMaxWidth(),
            ) {
                StatusHeader(
                    status = vpnState.status
                )
            }

            SelectionContainer(
                modifier = Modifier.weight(2f)
                    .fillMaxWidth(),
            ) {
                Content(
                    state = vpnState,
                    fieldStates = loginFieldStates
                )
            }

            ActionButton(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
                    .fillMaxWidth(),
                loginFieldStates = loginFieldStates,
                onClick = viewModel::loadVPNState
            )
        }
    }
}

@Composable
fun StatusHeader(status: VPNStatus, modifier: Modifier = Modifier) {
    val statusText = status.toString()
    Box(
        modifier = modifier
            .border(3.dp, Color.Gray, RoundedCornerShape(24.dp))
            .padding(horizontal = 5.dp, vertical = 5.dp),
        contentAlignment = BiasAlignment(0f, -0.1f)
    ) {
        Text(
            text = statusText,
            fontSize = 50.sp,
            color = status.getColor(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Content(state: VPNState, fieldStates: LoginFieldStates, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(3.dp, Color.Gray, RoundedCornerShape(24.dp))
            .padding(horizontal = 40.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (VPNStatus.DISCONNECTED == state.status) {
            val focusManager = LocalFocusManager.current
            val fieldModifier = Modifier
                .fillMaxWidth(0.75f)
                .onPreviewKeyEvent { event ->
                    // Check if the Tab key was pressed
                    if (event.key == Key.Tab && event.type == KeyEventType.KeyDown) {
                        // Shift + Tab moves backward, Tab moves forward
                        if (event.isShiftPressed) {
                            focusManager.moveFocus(FocusDirection.Previous)
                        } else {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        true // Consume the event so no "\t" is added
                    } else {
                        false // Let other keys pass through
                    }
                }
            val fieldKeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            OutlinedTextField(
                label = { Text("Username") },
                modifier = fieldModifier,
                keyboardOptions = fieldKeyboardOptions,
                state = fieldStates.username,
            )
            // TODO: Add configuration about whether to remember password
            // TODO: Add a toggle icon about whether to show the password
            OutlinedTextField(
                label = { Text("Password") },
                modifier = fieldModifier,
                keyboardOptions = fieldKeyboardOptions,
                outputTransformation = PasswordHidingTransformation(),
                state = fieldStates.password,
            )
            OutlinedTextField(
                label = { Text("One Time Password (OTP)") },
                modifier = fieldModifier,
                keyboardOptions = fieldKeyboardOptions,
                state = fieldStates.oneTimePassword,
            )
        } else {
            val scrollStateVertical = rememberScrollState()
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = state.content ?: "",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .verticalScroll(scrollStateVertical)
                        .padding(end = 6.dp),
                    softWrap = true
                )
                VerticalScrollbar(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd),
                    style = defaultScrollbarStyle().copy(
                        unhoverColor = Color.Black.copy(alpha = 0.25f),
                        hoverColor = Color.Black.copy(alpha = 0.75f),
                        thickness = 6.dp
                    ),
                    adapter = rememberScrollbarAdapter(scrollStateVertical)
                )
            }
        }
    }
}

// FIXME: Somehow deal with starting the vpn
// FIXME: Just show a loading circle while the connection is happening
@Composable
fun ActionButton(
    viewModel: VPNViewModel,
    loginFieldStates: LoginFieldStates,
    modifier: Modifier = Modifier,
    onClick: (outputFlow: String) -> Unit
) {
    val buttonData = viewModel.getButtonData(loginFieldStates)
    Button(
        onClick = { onClick(buttonData.action()) },
        modifier = modifier.pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))),
    ) {
        Text(
            text = buttonData.text,
            fontSize = 40.sp,
        )
    }
}

fun VPNStatus.getColor(): Color {
    return when (this) {
        VPNStatus.CONNECTED -> Color(0xFF0A9548)
        VPNStatus.RECONNECTING -> Color.Blue
        VPNStatus.DISCONNECTED -> Color.Red
        else -> Color.Black
    }
}

@Composable
fun VPNViewModel.getButtonData(loginFieldStates: LoginFieldStates): ButtonData {
    val status = this.getStateFlow().collectAsState().value.status
    return when (status) {
        VPNStatus.CONNECTED -> ButtonData("Disconnect", Color.Black, VPNActions::stop)
        VPNStatus.RECONNECTING -> ButtonData("Disconnect", Color.Black, VPNActions::stop)
        VPNStatus.DISCONNECTED -> ButtonData("Connect", Color.Black) {
            VPNActions.start(
                loginFieldStates.username.getTextAsString(),
                loginFieldStates.password.getTextAsString(),
                loginFieldStates.oneTimePassword.getTextAsString()
            )
        }

        else -> ButtonData("NOOP", Color.Black) { "" }
    }
}

class PasswordHidingTransformation : OutputTransformation {
    override fun TextFieldBuffer.transformOutput() {
        if (length > 0)
            replace(0, length - 1, "*".repeat(length))
    }
}

data class ButtonData(val text: String, val color: Color, val action: () -> String)

data class LoginFieldStates(
    val username: TextFieldState,
    val password: TextFieldState,
    val oneTimePassword: TextFieldState
) {
    companion object {
        @Composable
        fun empty(): LoginFieldStates {
            return LoginFieldStates(
                username = rememberTextFieldState(),
                password = TextFieldState(),
                oneTimePassword = TextFieldState()
            )
        }
    }
}

fun TextFieldState.getTextAsString(): String {
    return this.text.toString()
}