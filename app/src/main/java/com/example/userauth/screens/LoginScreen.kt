package com.example.userauth.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.userauth.data.User
import com.example.userauth.data.UserDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userDao = UserDao(context) // Use UserDao to save users locally

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !isValidEmail(email)
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError) {
            Text("Please enter a valid email", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = password.length < 6
            },
            label = { Text("Password") },
            isError = passwordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError) {
            Text("Password must be at least 6 characters", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!emailError && !passwordError && email.isNotEmpty() && password.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid
                                if (userId != null) {
                                    firestore.collection("users").document(userId).get()
                                        .addOnSuccessListener { document ->
                                            val role = document.getString("role") ?: "normal"

                                            // Fetch all users and save locally
                                            fetchAndSaveUsers(firestore, userDao, context) {
                                                onLoginSuccess(role)
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error fetching user role", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = onRegisterClick) {
            Text("Don't have an account? Register here")
        }
    }
}

private fun fetchAndSaveUsers(
    firestore: FirebaseFirestore,
    userDao: UserDao,
    context: android.content.Context,
    onComplete: () -> Unit
) {
    userDao.clearAllUsers()
    firestore.collection("users").get()
        .addOnSuccessListener { result ->
            val users = result.documents.mapNotNull { document ->
                val uid = document.id
                val email = document.getString("email") ?: return@mapNotNull null
                val role = document.getString("role") ?: return@mapNotNull null
                User(uid = uid, email = email, role = role)
            }
            CoroutineScope(Dispatchers.IO).launch {
                userDao.insertUsers(users)
                withContext(Dispatchers.Main) {
                    onComplete()
                }
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show()
        }
}
