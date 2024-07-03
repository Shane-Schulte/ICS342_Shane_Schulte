package com.example.ics342shaneschultesapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                Surface(color = MaterialTheme.colors.background) {
                    TodoApp()
                }
            }
        }
    }
}

@Composable
fun TodoApp() {
    var todoItems by remember { mutableStateOf(listOf<TodoItem>()) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo List") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding).padding(12.dp)) {
            TodoList(todoItems) { item, isChecked ->
                todoItems = todoItems.map {
                    if (it == item) it.copy(isCompleted = isChecked) else it
                }
            }

            if (showDialog) {
                AddTodoDialog(
                    onDismiss = { showDialog = false },
                    onSave = { newItem ->
                        todoItems = todoItems + newItem
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun TodoList(todoItems: List<TodoItem>, onCheckedChange: (TodoItem, Boolean) -> Unit) {
    LazyColumn {
        items(todoItems) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(item.name, modifier = Modifier.weight(1f))
                Checkbox(
                    checked = item.isCompleted,
                    onCheckedChange = { isChecked -> onCheckedChange(item, isChecked) }
                )
            }
        }
    }
}

@Composable
fun AddTodoDialog(onDismiss: () -> Unit, onSave: (TodoItem) -> Unit) {
    var text by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add Todo") },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Todo Item") },
                    trailingIcon = {
                        IconButton(onClick = { text = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear text")
                        }
                    }
                )
                if (showError) {
                    Text("Please enter a todo item", color = MaterialTheme.colors.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (text.isBlank()) {
                    showError = true
                } else {
                    onSave(TodoItem(name = text))
                    showError = false
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

data class TodoItem(val name: String, val isCompleted: Boolean = false)

private val Purple200 = Color(0xFFBB86FC)
private val Purple500 = Color(0xFF6200EE)
private val Purple700 = Color(0xFF3700B3)
private val Teal200 = Color(0xFF03DAC5)

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200
)

@Composable
fun TodoListTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private val Typography = Typography(
    body1 = androidx.compose.ui.text.TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

private val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(4.dp),
    large = RoundedCornerShape(0.dp)
)
