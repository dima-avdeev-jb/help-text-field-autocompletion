import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.InputModeManager
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import java.awt.event.KeyEvent
import java.time.LocalTime


internal val androidx.compose.ui.input.key.KeyEvent.isDirectionUp: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_UP

internal val androidx.compose.ui.input.key.KeyEvent.isDirectionDown: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_DOWN

internal val androidx.compose.ui.input.key.KeyEvent.isDirectionRight: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_RIGHT

internal val androidx.compose.ui.input.key.KeyEvent.isDirectionLeft: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_LEFT

internal val androidx.compose.ui.input.key.KeyEvent.isHome: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_HOME

internal val androidx.compose.ui.input.key.KeyEvent.isMoveEnd: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_END

internal val androidx.compose.ui.input.key.KeyEvent.isPgUp: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_PAGE_UP

internal val androidx.compose.ui.input.key.KeyEvent.isPgDn: Boolean
    get() = key.nativeKeyCode == KeyEvent.VK_PAGE_DOWN

@ExperimentalComposeUiApi
private fun preHandlePopupOnKeyEvent(
    keyEvent: androidx.compose.ui.input.key.KeyEvent
): Boolean {
    return if (keyEvent.type == KeyEventType.KeyDown && keyEvent.awtEventOrNull?.keyCode == KeyEvent.VK_ESCAPE)
        true
    else if (keyEvent.type == KeyEventType.KeyDown)
        when {
            keyEvent.isDirectionDown -> true

            keyEvent.isDirectionUp -> true

            else -> false
        }
    else false
}


@ExperimentalComposeUiApi
private fun handlePopupOnKeyEvent(
    keyEvent: androidx.compose.ui.input.key.KeyEvent,
    onDismissRequest: () -> Unit,
    focusManager: FocusManager,
    inputModeManager: InputModeManager
): Boolean {
    return if (keyEvent.type == KeyEventType.KeyDown && keyEvent.awtEventOrNull?.keyCode == KeyEvent.VK_ESCAPE) {
        onDismissRequest()
        true
    } else if (keyEvent.type == KeyEventType.KeyDown) {
        when {
            keyEvent.isDirectionDown -> {
                inputModeManager.requestInputMode(InputMode.Keyboard)
                focusManager.moveFocus(FocusDirection.Next)
                true
            }

            keyEvent.isDirectionUp -> {
                inputModeManager.requestInputMode(InputMode.Keyboard)
                focusManager.moveFocus(FocusDirection.Previous)
                true
            }

            else -> false
        }
    } else {
        false
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ComponentShow() {
    Column {
        var text by remember { mutableStateOf("") }
        var isExpand by remember { mutableStateOf(false) }
        val focusManagerCurrent = LocalFocusManager.current
        OutlinedTextField(
            text,
            onValueChange = { text = it },
            placeholder = { Text("What") },
            modifier = Modifier.onFocusChanged {
                isExpand = it.isFocused
                println("ComponentShow, it.isFocused ${it.isFocused}")
            }.onKeyEvent {
                println("ComponentShow, OutlinedTextField -- onKeyEvent $it")
                false
            })
        if (isExpand) {
            var focusManager: FocusManager? by mutableStateOf(null)
            var inputModeManager: InputModeManager? by mutableStateOf(null)
            val onDismissRequest: () -> Unit = { isExpand = false; focusManagerCurrent.clearFocus() }
            Popup(popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset = IntOffset(anchorBounds.left, anchorBounds.bottom + 10)
            }, onDismissRequest = onDismissRequest, focusable = true, onPreviewKeyEvent = {
                println("ComponentShow, onPreviewKeyEvent: $it")
                preHandlePopupOnKeyEvent(it)
            }, onKeyEvent = {
                val r = handlePopupOnKeyEvent(
                    it, onDismissRequest, focusManager!!, inputModeManager!!
                )
                println("ComponentShow, onKeyEvent: $it, $r")
                r
            }) {
                focusManager = LocalFocusManager.current
                inputModeManager = LocalInputModeManager.current
                Box(Modifier.size(250.dp).background(Color.White).shadow(4.dp, RoundedCornerShape(15))) {

                }
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        ComponentShow()
    }
}
