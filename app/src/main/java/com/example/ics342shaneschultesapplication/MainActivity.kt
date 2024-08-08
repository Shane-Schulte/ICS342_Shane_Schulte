package com.example.ics342shaneschultesapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.text.input.PasswordVisualTransformation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoListTheme {
                val navController = rememberNavController()
                var user by remember { mutableStateOf<User?>(null) }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(onLoginSuccess = { loggedInUser ->
                            user = loggedInUser
                            navController.navigate("todo")
                        }, onCreateAccount = {
                            navController.navigate("create_account")
                        })
                    }
                    composable("create_account") {
                        CreateAccountScreen(onAccountCreated = { newUser ->
                            user = newUser
                            navController.navigate("todo")
                        }, onCancel = {
                            navController.popBackStack()
                        })
                    }
                    composable("todo") {
                        if (user != null) {
                            TodoApp(user = user!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: (User) -> Unit,
    onCreateAccount: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by loginViewModel.loginState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { loginViewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create an Account")
        }
        if (loginState is LoginViewModel.LoginState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        if (loginState is LoginViewModel.LoginState.Error) {
            Text(
                text = (loginState as LoginViewModel.LoginState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp))
        }
        if (loginState is LoginViewModel.LoginState.Success) {
            LaunchedEffect(loginState) {
                onLoginSuccess((loginState as LoginViewModel.LoginState.Success).user)
            }
        }
    }
}

@Composable
fun CreateAccountScreen(
    onAccountCreated: (User) -> Unit,
    onCancel: () -> Unit,
    createAccountViewModel: CreateAccountViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val createAccountState by createAccountViewModel.createAccountState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { createAccountViewModel.createAccount(name, email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
        if (createAccountState is CreateAccountViewModel.CreateAccountState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
        if (createAccountState is CreateAccountViewModel.CreateAccountState.Error) {
            Text(
                text = (createAccountState as CreateAccountViewModel.CreateAccountState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        if (createAccountState is CreateAccountViewModel.CreateAccountState.Success) {
            LaunchedEffect(createAccountState) {
                onAccountCreated((createAccountState as CreateAccountViewModel.CreateAccountState.Success).user)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(user: User, todoListViewModel: ToDoListViewModel = viewModel()) {
    val todoItems by todoListViewModel.todoListState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(user) {
        todoListViewModel.fetchTodos()
    }

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
                todoListViewModel.updateTodoStatus(item.id, isChecked)
            }

            if (showDialog) {
                AddTodoDialog(
                    onDismiss = { showDialog = false },
                    onSave = { newItem ->
                        todoListViewModel.createTodo(newItem.description)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun TodoList(todoItems: List<ToDoItem>, onCheckedChange: (ToDoItem, Boolean) -> Unit) {
    LazyColumn {
        items(todoItems) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(item.description, modifier = Modifier.weight(1f))
                Checkbox(
                    checked = item.completed,
                    onCheckedChange = { isChecked -> onCheckedChange(item, isChecked) }
                )
            }
        }
    }
}

@Composable
fun AddTodoDialog(onDismiss: () -> Unit, onSave: (ToDoItem) -> Unit) {
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
                    Text("Please enter a todo item", color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (text.isBlank()) {
                    showError = true
                } else {
                    onSave(ToDoItem.create(description = text))
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

private val Purple200 = Color(0xFFBB86FC)
private val Purple500 = Color(0xFF6200EE)
private val Purple700 = Color(0xFF3700B3)
private val Teal200 = Color(0xFF03DAC5)

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    primaryContainer = Purple700,
    secondary = Teal200
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    primaryContainer = Purple700,
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
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}

private val Typography = Typography(
    bodyLarge = TextStyle(
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
