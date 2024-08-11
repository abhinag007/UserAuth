package com.example.userauth.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class UserDao(context: Context) {
    private val dbHelper = UserDatabaseHelper(context)

    fun clearAllUsers() {
        val db = dbHelper.writableDatabase
        db.delete(UserDatabaseHelper.TABLE_USERS, null, null)
        db.close()
    }

    fun insertUser(user: User) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserDatabaseHelper.COLUMN_UID, user.uid)
            put(UserDatabaseHelper.COLUMN_EMAIL, user.email)
            put(UserDatabaseHelper.COLUMN_ROLE, user.role)
        }
        db.insert(UserDatabaseHelper.TABLE_USERS, null, values)
        db.close()
    }

    fun insertUsers(users: List<User>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            for (user in users) {
                val values = ContentValues().apply {
                    put(UserDatabaseHelper.COLUMN_UID, user.uid)
                    put(UserDatabaseHelper.COLUMN_EMAIL, user.email)
                    put(UserDatabaseHelper.COLUMN_ROLE, user.role)
                }
                db.insert(UserDatabaseHelper.TABLE_USERS, null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            arrayOf(UserDatabaseHelper.COLUMN_UID, UserDatabaseHelper.COLUMN_EMAIL, UserDatabaseHelper.COLUMN_ROLE),
            null, null, null, null, null
        )
        while (cursor.moveToNext()) {
            val uid = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_UID))
            val email = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_EMAIL))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ROLE))
            users.add(User(uid, email, role))
        }
        cursor.close()
        db.close()
        return users
    }

    fun getUserById(uid: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            arrayOf(UserDatabaseHelper.COLUMN_UID, UserDatabaseHelper.COLUMN_EMAIL, UserDatabaseHelper.COLUMN_ROLE),
            "${UserDatabaseHelper.COLUMN_UID}=?",
            arrayOf(uid),
            null, null, null
        )
        return if (cursor.moveToFirst()) {
            val email = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_EMAIL))
            val role = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ROLE))
            User(uid, email, role)
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }
}
