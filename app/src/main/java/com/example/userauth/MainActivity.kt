package com.example.userauth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.userauth.data.UserDao
import com.example.userauth.ui.theme.UserAuthTheme
import com.example.userauth.screens.DashboardScreen
import com.example.userauth.screens.LoginScreen
import com.example.userauth.screens.RegisterScreen
import com.example.userauth.screens.UserDetailScreen
import com.example.userauth.screens.UserListScreen
import com.example.userauth.screens.UserManagementScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserAuthTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainNavigation()
                }
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val userDao = remember { UserDao(context) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    navController.navigate("dashboard/$role") {
                        // Clear navigation stack
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        launchSingleTop = true                    }
                },
                onRegisterClick = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                navController = navController,
                onRegisterSuccess = {
                    navController.navigate("dashboard/normal") {
                        // Clear navigation stack
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "normal"
            DashboardScreen(
                role = role,
                onNavigate = { screen ->
                    when (screen) {
                        "Dashboard" -> navController.navigate("dashboard/$role")
                        "User List" -> navController.navigate("userList")
                        "User Management" -> navController.navigate("userManagement")
                        "Logout" -> {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                // Clear navigation stack
                                popUpTo(navController.graph.id) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        else -> { /* Handle other navigation */ }
                    }
                }
            )
        }
        composable("userList") {
            UserListScreen(                navController = navController,
                userDao = userDao) // Pass UserDao to UserListScreen
        }
        composable("userManagement") {
            UserManagementScreen(navController = navController,userDao = userDao) // Pass UserDao to UserManagementScreen
        }
        composable("userDetail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserDetailScreen(userId = userId, userDao = userDao, navController = navController)
        }

    }
}
