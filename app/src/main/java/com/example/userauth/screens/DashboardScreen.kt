package com.example.userauth.screens

import Sidebar
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(role: String, onNavigate: (String) -> Unit) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Sidebar(role = role, onNavigate = { route ->
                onNavigate(route)
                scope.launch { drawerState.close() }
            })
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Dashboard") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Open Sidebar")
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Welcome to the Dashboard", style = MaterialTheme.typography.headlineMedium)

                        Spacer(modifier = Modifier.height(20.dp))

                        when (role) {
                            "admin" -> Text("Admin Dashboard Content")
                            "user" -> Text("User Dashboard Content")
                            else -> Text("Unknown Role")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { /* Handle action */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Do Something")
                        }
                    }
                }
            }
        }
    )
}