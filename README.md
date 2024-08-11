# UserAuth App

UserAuth is an Android application built with Jetpack Compose and Firebase that provides authentication, user management, and role-based access control. This README outlines the features, setup instructions, and common usage details.

## Features

- **User Authentication**: Sign in and register users using Firebase Authentication.
- **Role-Based Access Control**: Users are assigned roles (e.g., normal, admin) that determine their access level.
- **User Management**: Admins can manage users, including viewing the list and deleting users.
- **Dashboard Navigation**: Role-based navigation to different screens including Dashboard, User List, and User Management.
- **Pull-to-Refresh**: Refresh user data from Firebase and update local storage.
- **Navigation**: Clear navigation stack upon logout to prevent unauthorized access to protected screens.

## Technologies

- **Jetpack Compose**: UI toolkit for building native Android UIs.
- **Firebase**: Authentication, Firestore for database.
- **Kotlin**: Programming language used for Android development.
- **SQLite**: Local database for storing user data.
- **Navigation**: Handling app navigation with Jetpack Compose Navigation.

## Setup

### Prerequisites

- Android Studio
- Java Development Kit (JDK)
- Firebase Project with Authentication and Firestore enabled

  ## Common Tasks

### User Authentication
- **Login**: User logs in with email and password.
- **Register**: New user registration with email, password, and role.
- **Logout**: Clear navigation stack and navigate to the login screen.

### User Management
- **View Users**: Display a list of users with roles.
- **Delete Users**: Admins can delete users from Firebase and local storage.

### Navigation
- **Role-Based Navigation**: Navigate to screens based on user roles (normal, admin).
- **Clear Navigation Stack**: Clear the navigation stack upon logout to prevent unauthorized access.

### Pull-to-Refresh
- **Refresh User List**: Update the user list by fetching data from Firebase and saving it locally.

## Code Structure
- **MainActivity.kt**: Entry point of the application, sets up navigation.
- **UserDao.kt**: Data Access Object for interacting with the local database.
- **UserManagementScreen.kt**: Screen for managing users.
- **UserListScreen.kt**: Screen for viewing the list of users.
- **LoginScreen.kt**: Screen for user login.
- **RegisterScreen.kt**: Screen for user registration.
- **DashboardScreen.kt**: Main screen after login, with role-based navigation.


