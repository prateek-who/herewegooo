package com.example.herewegooo

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.herewegooo.data.model.UserViewModel


@Composable
fun AppNavigate(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit,
){
    val easeIn = 1000
    var loginReset = Int

    NavHost(
        navController = navController,
        startDestination = "starthere",
        modifier = modifier
    ){
        composable(
            route = "starthere",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            }
        ) {
            Login(
                navController = navController,
                userViewModel = userViewModel,
                onShowSnackbar = onShowSnackbar
                )
        }
        composable(
            route = "home",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            }
        ){
            MainScreen(navController = navController)
        }

        composable(route = "adminPanel"){
            AdminPanel(onShowSnackbar = onShowSnackbar)
        }

        composable(
            route = "floorNumber/{floornumber}",
            arguments = listOf(
                navArgument("floornumber"){
                    type = NavType.StringType
                }
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            popEnterTransition = {
                slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = tween(durationMillis = easeIn, easing = FastOutSlowInEasing)
                )
            }
        ){
            entry ->
            val floor = entry.arguments?.getString("floornumber")?:"0"
            EightFloor(navController = navController, floor)
        }

        composable(
            route = "floorNumber/{floornumber}/roomNumber/{roomnumber}",
            arguments = listOf(
                navArgument("floornumber"){
                    type = NavType.StringType
                },
                navArgument("roomnumber"){
                    type = NavType.StringType
                }
            )
        ) {
            entry ->
            val floor = entry.arguments?.getString("floornumber")?:"0"
            val room = entry.arguments?.getString("roomnumber")?:"0"
            WeInRoom(floor, room, userViewModel = userViewModel, onShowSnackbar = onShowSnackbar)
        }

        composable(
            route = "profile"
        ) {
            SelfProfile(
                navController = navController,
                userViewModel = userViewModel
            )
        }

        composable(
            route = "timetable"
        ) {
            Timetable(userViewModel, onShowSnackbar)
        }
    }
}
