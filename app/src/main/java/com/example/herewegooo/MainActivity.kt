package com.example.herewegooo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.herewegooo.data.model.UserViewModel
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

val bungeeFont = FontFamily(
    Font(R.font.bungeehairline_regular)
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.isAppearanceLightStatusBars = false

        setContent {
            HerewegoooTheme {
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    isLoading = false
                }
                if (isLoading) {
                    CustomSplashScreen()
                } else {
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
                                } else {
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
                        containerColor = Color(0xFF1E1E2E).copy(alpha = 1f),
                        snackbarHost = {
                            CustomSnackbar(snackbarHostState = snackbarHostState, snackbarType = snackbarType.value, modifier = Modifier)
//                            SnackbarHost(snackbarHostState) { data ->
//                                val contentColor = when (snackbarType.value) {
//                                    SnackbarType.ERROR -> Color(0xFFFF3B30)
//                                    SnackbarType.SUCCESS ->Color(0xFF34C759)
//                                }
//                                Snackbar(
//                                    snackbarData = data,
//                                    containerColor = Color.Black,
//                                    contentColor = contentColor,
//                                    shape = RoundedCornerShape(25.dp),
//                                )
//                            }
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
                        Box(
                            modifier = Modifier.padding(
                                // Only apply bottom padding when the bottom bar is showing
                                bottom = if (currentRoute != "starthere" && currentRoute != null)
                                    innerPadding.calculateBottomPadding()
                                else
                                    0.dp,
                                // Keep other padding values
                                top = innerPadding.calculateTopPadding(),
                                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
                            )
                        ) {
                            AppNavigate(
                                navController = navController,
                                modifier = Modifier,
                                userViewModel = userViewModel,
                                onShowSnackbar = { message, type ->
                                    snackbarType.value = type
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = message,
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
}


@Composable
fun CustomSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    snackbarType: SnackbarType = SnackbarType.SUCCESS
) {
    // Define color and icon based on snackbar type
    val (backgroundColor, contentColor) = when (snackbarType) {
        SnackbarType.SUCCESS -> Triple(
            Color(0xFF2ecc71).copy(alpha = 0.9f),
            Color.White,
            painterResource(id = R.drawable.checkcircle)
        )
        SnackbarType.ERROR -> Triple(
            Color(0xFFe74c3c).copy(alpha = 0.9f),
            Color.White,
            painterResource(id = R.drawable.transparent)
        )
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) { data ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 30.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = data.visuals.message,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = funnelFont
                    ),
                    modifier = Modifier.weight(1f)
                )

                // Dismiss button here
                IconButton(
                    onClick = { data.dismiss() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = contentColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


@Composable
fun CustomSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2E)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splashmaybe),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .fillMaxWidth(0.8f)  // Take 80% of screen width
                .aspectRatio(2f),    // Adjust aspect ratio as needed
            contentScale = ContentScale.Fit
        )
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