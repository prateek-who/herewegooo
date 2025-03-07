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
//        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE


//        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
////            val currentTime = System.currentTimeMillis()
////            if (currentTime - lastInsetsUpdate > debounceDelay) {
//                if (insets.isVisible(WindowInsetsCompat.Type.navigationBars())
//                    || insets.isVisible(WindowInsetsCompat.Type.statusBars())
//                ) {
////                binding.toggleFullscreenButton.setOnClickListener {
//                    insetsController.hide(WindowInsetsCompat.Type.navigationBars())
//                    insets.getInsets(WindowInsetsCompat.Type.navigationBars().toColor())
//                    if (currentFocus == null) {
//                        insetsController.hide(WindowInsetsCompat.Type.ime())
//                    }
////                }
//                } else {
////                binding.toggleFullscreenButton.setOnClickListener {
//                    insetsController.show(WindowInsetsCompat.Type.navigationBars())
//                    if (currentFocus != null) {
//                        insetsController.show(WindowInsetsCompat.Type.ime())
//                    }
////                }
//            }
//            ViewCompat.onApplyWindowInsets(view, insets)
//        }

        insetsController.isAppearanceLightStatusBars = false
//        insetsController.hide(WindowInsetsCompat.Type.systemBars())
//        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
//        WindowInsetsCompat.Type.navigationBars()

        setContent {
            HerewegoooTheme {
                val navController = rememberNavController()
                val userViewModel: UserViewModel = viewModel()

                var selectedIndex by remember { mutableIntStateOf(0) }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val snackbarType = remember { mutableStateOf(SnackbarType.SUCCESS) }
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding(),
                    snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                            val contentColor = if (snackbarType.value == SnackbarType.ERROR) Color.Red else Color(0xFF187212)
                            Snackbar(
                                snackbarData = data,
                                containerColor = Color.Black,
                                contentColor = contentColor,
                                shape = RoundedCornerShape(25.dp),
                            )
                        }
                    },
                    bottomBar = {
                        if (currentRoute != "starthere") {
                            BottomNavBar(
                                selectedIndex = selectedIndex,
                                onItemSelected = { index, navItem ->
                                    selectedIndex = index
                                    navController.navigate(navItem.forRoute) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            inclusive = true
                                        }
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
                            onShowSnackbar = { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = message,
                                        actionLabel = "x",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        )
//                        WeInRoom("7","706")
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