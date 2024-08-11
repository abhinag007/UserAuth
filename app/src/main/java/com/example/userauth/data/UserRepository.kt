package com.example.userauth.data

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

suspend fun refreshUsersList(
    userDao: UserDao,
    auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    context: Context
): List<User>? {
    return try {
        // Fetch users from Firebase Firestore
        val usersCollection = firestore.collection("users")
        val usersSnapshot = usersCollection.get().await()
        val remoteUsers = usersSnapshot.documents.mapNotNull { document ->
            val email = document.getString("email")
            val role = document.getString("role")
            val uid = document.id
            email?.let { email ->
                User(uid = uid, email = email, role = role ?: "normal")
            }
        }

        // Update local database
        userDao.clearAllUsers() // Clear existing users
        userDao.insertUsers(remoteUsers)

        remoteUsers // Return the updated list
    } catch (e: Exception) {
        // Handle exceptions (e.g., show error message)
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error refreshing users: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        null
    }
}
