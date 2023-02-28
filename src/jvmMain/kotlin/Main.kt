import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        var textState by remember { mutableStateOf("") }
        AutoCompleteText(textState, onValueChange = { textState = it }, onOptionSelected = { textState = it },
            suggestions = listOf("Cat", "Cow", "Cowboy", "Candy")
        )
    }
}

@Composable
fun AutoCompleteText(
    value: String,
    onValueChange: (String) -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    suggestions: List<String> = emptyList()
) {
    Column {
        TextField(
            value = value,
            onValueChange = { text -> if (text !== value) onValueChange(text) },
            modifier = Modifier.fillMaxWidth(),
            label = label,
        )
        if (value.isNotEmpty()) {
            val filtered = suggestions.filter { it.startsWith(value, ignoreCase = true) && it != value }
            if (filtered.isNotEmpty()) {
                DropdownMenu(
                    expanded = suggestions.isNotEmpty(),
                    onDismissRequest = { },
                    modifier = Modifier.fillMaxWidth(),
                    // This line here will accomplish what you want
                    focusable = false,
                ) {
                    filtered.forEach { label ->
                        DropdownMenuItem(onClick = {
                            onOptionSelected(label)
                        }) {
                            Text(text = label)
                        }
                    }
                }

            }
        }
    }
}
