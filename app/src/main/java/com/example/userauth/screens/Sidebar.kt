import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun Sidebar(role: String, onNavigate: (String) -> Unit) {
    // Calculate 3/4 of the screen width
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val sidebarWidth = screenWidth * 0.75f

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(sidebarWidth)
            .background(Color.White) // Set background color to white
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Menu", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        MenuItem("Dashboard", onNavigate)
        MenuItem("User List", onNavigate)

        if (role == "admin") {
            MenuItem("User Management", onNavigate)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        MenuItem("Logout", onNavigate)
    }
}

@Composable
fun MenuItem(label: String, onNavigate: (String) -> Unit) {
    Button(
        onClick = { onNavigate(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(label)
    }
}
