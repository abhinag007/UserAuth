package com.example.userauth.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.userauth.data.User
import com.example.userauth.data.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
@Composable
fun RegisterScreen(navController: NavController, onRegisterSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("normal") }

    val scrollState = rememberScrollState()
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val userDao = UserDao(context) // Use the UserDao directly

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Register", style = MaterialTheme.typography.headlineMedium)

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

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordError = confirmPassword != password
            },
            label = { Text("Confirm Password") },
            isError = confirmPasswordError,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (confirmPasswordError) {
            Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Select Role", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RoleRadioButton(
                role = "admin",
                selectedRole = selectedRole,
                onRoleChange = { selectedRole = it }
            )
            RoleRadioButton(
                role = "normal",
                selectedRole = selectedRole,
                onRoleChange = { selectedRole = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (!emailError && !passwordError && !confirmPasswordError &&
                    email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                    registerUser(email, password, selectedRole, auth, firestore, userDao, context) {
                        // Navigate to login screen after successful registration
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login here")
        }
    }
}

@Composable
fun RoleRadioButton(role: String, selectedRole: String, onRoleChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = role == selectedRole,
            onClick = { onRoleChange(role) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(role.capitalize())
    }
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
private fun registerUser(
    email: String,
    password: String,
    role: String,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    userDao: UserDao,
    context: android.content.Context,
    onRegisterSuccess: () -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                val user = hashMapOf(
                    "email" to email,
                    "role" to role
                )
                // Save user role and email to Firestore
                firestore.collection("users").document(userId)
                    .set(user)
                    .addOnSuccessListener {
                        val localUser = User(uid = userId, email = email, role = role)
                        CoroutineScope(Dispatchers.IO).launch {
                            userDao.insertUser(localUser)
                            withContext(Dispatchers.Main) {
                                onRegisterSuccess()
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Error saving user data", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }
}
