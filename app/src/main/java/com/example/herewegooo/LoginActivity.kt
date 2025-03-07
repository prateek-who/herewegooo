package com.example.herewegooo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults.contentColor
import androidx.compose.material3.SnackbarDefaults.shape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.ProfileRole
import com.example.herewegooo.network.singInUser
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.util.rootCause
import kotlinx.coroutines.launch
import com.example.herewegooo.ui.theme.HerewegoooTheme


@Composable
fun Login(navController: NavController,
          userViewModel: UserViewModel,
          onShowSnackbar: (String) -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var passText by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val snackbarType = remember { mutableStateOf(SnackbarType.ERROR) }
    val coroutineScope = rememberCoroutineScope()

    val client = remember { supabaseClient() }

    val boxColors = OutlinedTextFieldDefaults.colors(
        unfocusedTextColor = Color.White,
        focusedContainerColor = Color.White,
        focusedTextColor = Color.Black,
        focusedLabelColor = Color.White,
        focusedBorderColor = Color.LightGray,
        cursorColor = Color.Black
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xff000000),
                            Color(0xFF362D22),
                        )
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = emailText,
                onValueChange = { emailText = it },
                modifier = Modifier
                    .height(65.dp)
                    .offset(y = 35.dp),
                shape = RoundedCornerShape(25.dp),
                colors = boxColors,
                label = {
                    Text(
                        text = "Email / Username",
                        fontFamily = funnelFont
                    )
                },
            )
            OutlinedTextField(
                value = passText,
                onValueChange = { passText = it },
                modifier = Modifier
                    .height(65.dp)
                    .offset(y = 47.dp),
                shape = RoundedCornerShape(25.dp),
                colors = boxColors,
                label = {
                    Text(
                        text = "Password",
                        fontFamily = funnelFont
                    )

                }
            )

            Button(
                modifier = Modifier
                    .height(50.dp)
                    .offset(y = 70.dp),
                onClick = {
                    if (emailText.isBlank() || passText.isBlank()) {
//                    coroutineScope.launch {
                        onShowSnackbar("Email/ Password cannot be empty!")
//                    }
                    } else {
                        coroutineScope.launch {
                            singInUser(client, emailText, passText)
                                .onSuccess {
//                                    val session = client.auth.getUser()
//                                    println(session)
                                    val userId =
                                        client.auth.currentUserOrNull()?.id ?: return@onSuccess

//                                println(userId)

                                    val user = client.from("users")
                                        .select(Columns.list("role, username")) {
                                            filter {
                                                eq("user_id", userId)
                                            }
                                        }.decodeSingle<ProfileRole>()

                                    userViewModel.userRole = user.role
                                    userViewModel.userName = user.username

//                                BottomNavBar().userRole = user.role
//                                println(user.role)
//                                println(user.username)

                                    if(user.role == "admin"){
                                        navController.navigate(route = "adminPanel") {
                                            popUpTo(route = "starthere") {
                                                inclusive = true
                                            }
                                        }
                                    }else {
                                        navController.navigate(route = "home") {
                                            popUpTo(route = "starthere") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                                .onFailure { error ->
                                    val errorType = error::class.simpleName ?: "Unknown Error"
                                    println(errorType)
                                    val message = when (error) {
                                        is AuthWeakPasswordException -> "Your password is too weak!"
                                        else -> "Sign-in failed. Please try again."
                                    }
//                                println(error)
                                    onShowSnackbar("Sign-in failed: $message")
                                }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Sign In",
                    fontFamily = funnelFont,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                modifier = Modifier.offset(x = 110.dp, y = 260.dp),
                onClick = {
                    userViewModel.userRole = "student"
                    userViewModel.userName = "student"

                    navController.navigate(route = "home") {
                        popUpTo(route = "starthere") {
                            inclusive = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "Login as Student",
                    fontFamily = funnelFont,
                )
            }
        }
    }
}
