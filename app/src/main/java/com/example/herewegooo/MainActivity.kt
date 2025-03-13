package com.example.herewegooo

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathSegment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColor
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.ProfileRole
import com.example.herewegooo.ui.theme.HerewegoooTheme
import kotlinx.coroutines.launch


val funnelFont = FontFamily(
    Font(R.font.funneldisplay_variablefont)
)

val oswaldFont = FontFamily(
    Font(R.font.oswald_variable_font)
)

val karlaFont = FontFamily(
    Font(R.font.karla_variablefont)
)

val markazFont = FontFamily(
    Font(R.font.markazitext_variablefont)
)

val newsReaderFont = FontFamily(
    Font(R.font.newsreader_variablefont_opsz)
)

val stixtwoFont = FontFamily(
    Font(R.font.stixtwotext_variablefont)
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.isAppearanceLightStatusBars = false

        setContent {
            HerewegoooTheme {
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val selectedIndex = remember { mutableIntStateOf(0) }

                // Create a mapping of routes to indices for easier reference
                val routeToIndexMap = remember(userViewModel.userRole) {
                    if (userViewModel.userRole == "admin") {
                        mapOf(
                            "adminPanel" to 0,
                            "Profile" to 1
                        )
                    } else {
                        mapOf(
                            "Home" to 0,
                            "Timetable" to 1,
                            "Profile" to 2
                        )
                    }
                }

                // Update the selectedIndex whenever the route changes
                LaunchedEffect(currentRoute) {
                    currentRoute?.let { route ->
                        if (route != "starthere") {
                            if (userViewModel.index == 0) {
                                selectedIndex.intValue = 0
                                userViewModel.index += 1
                            }else {
                                routeToIndexMap[route]?.let { index ->
                                    selectedIndex.intValue = index
                                }
                            }
                        }
                    }
                }

                val snackbarType = remember { mutableStateOf(SnackbarType.SUCCESS) }
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    containerColor = Color(0xFF121218),
                    snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                            val contentColor = if (snackbarType.value == SnackbarType.ERROR) Color(0xFFFF3B30) else Color(0xFF34C759)
                            Snackbar(
                                snackbarData = data,
                                containerColor = Color.Black,
                                contentColor = contentColor,
                                shape = RoundedCornerShape(25.dp),
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute != "starthere" && currentRoute != null) {
                            BottomNavBar(
                                selectedIndex = selectedIndex.intValue,
                                onItemSelected = { index, navItem ->
                                    // Set the index before navigation
                                    selectedIndex.intValue = index

                                    // Simple navigation approach without popUpTo
                                    navController.navigate(navItem.forRoute) {
                                        // Only use launchSingleTop to avoid duplicates
                                        launchSingleTop = true
                                    }
                                },
                                userViewModel = userViewModel,
                                content = { }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigate(
                            navController = navController,
                            modifier = Modifier,
                            userViewModel = userViewModel,
                            onShowSnackbar = { message, type ->
                                snackbarType.value = type
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = "x",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val forRoute: String
)

enum class SnackbarType {
    SUCCESS, ERROR
}