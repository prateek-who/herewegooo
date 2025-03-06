package com.example.herewegooo

import android.os.Bundle
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.ui.theme.HerewegoooTheme


@Composable
fun AppNavigate(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onShowSnackbar: (String) -> Unit
){
    NavHost(
        navController = navController,
        startDestination = "starthere",
        modifier = modifier
    ){
        composable(route = "starthere") {
            Login(
                navController = navController,
                userViewModel = userViewModel,
                onShowSnackbar = onShowSnackbar
                )
        }
        composable(route = "home"){
            MainScreen(navController = navController)
        }

        composable(
            route = "floorNumber/{floornumber}",
            arguments = listOf(
                navArgument("floornumber"){
                    type = NavType.StringType
                }
            )
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
            WeInRoom(navController = navController, floor, room, userViewModel = userViewModel)
        }

        composable(
            route = "profile"
        ) {
            SelfProfile(navController = navController)
        }

        composable(
            route = "timetable"
        ) {
            Timetable()
        }
    }
}
