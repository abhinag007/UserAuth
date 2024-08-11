package com.example.userauth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.userauth.data.User
import com.example.userauth.data.UserDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(userId: String, userDao: UserDao, navController: NavController) {
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        user = userDao.getUserById(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            user?.let {
                Text("User Details", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Email: ${it.email}", style = MaterialTheme.typography.bodyLarge)
                Text("Role: ${it.role}", style = MaterialTheme.typography.bodyLarge)
                // Add more user details here
            } ?: run {
                Text("Loading...", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
