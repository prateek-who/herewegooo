package com.example.herewegooo

import android.net.http.NetworkException
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.annotation.RequiresExtension
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults.contentColor
import androidx.compose.material3.SnackbarDefaults.shape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import com.example.herewegooo.network.LoginDataOne
import com.example.herewegooo.network.LoginDataTwo
import com.example.herewegooo.network.singInUser
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.util.rootCause
import kotlinx.coroutines.launch
import com.example.herewegooo.ui.theme.HerewegoooTheme
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher


@Composable
fun Login(
    navController: NavController,
    userViewModel: UserViewModel,
    onShowSnackbar: (String, SnackbarType) -> Unit
) {
    // Original state variables
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // More vibrant color scheme
    val backgroundColor = Color(0xFF1E1E2E)  // Deep blue background
    val textColor = Color(0xFFF8F8FF)         // Bright white text
    val accentColor = Color(0xFF9676DB)       // Bright blue accent
    val buttonColor = Color(0xFF16B1AC)       // Teal button color
    val cardBackground = Color(0xFF272C3D)    // Slightly brighter card background
    val highlightColor = Color(0xFF72F2EB)    // Coral highlight for attention areas Color(0xFFFF7B89)

    // Updated field background colors to match the theme
    val unfocusedFieldColor = Color(0xFF1E2239)  // Darker version of cardBackground
    val focusedFieldColor = Color(0xFF222845)    // Slightly lighter when focused

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val client = remember { supabaseClient() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)  // Using updated backgroundColor
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .statusBarsPadding()
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
//                    .size(200.dp)
                    .height(150.dp)
                    .width(400.dp)
                    .offset(y = (-40).dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.logo),
//                    contentDescription = "App Logo",
//                    modifier = Modifier
//                        .size(350.dp)
//                        .padding(0.dp),
//                    tint = Color(0xFFE4FFFC)
//                )
//
//                // App name with more stylized text
//            Text(
//                text = "RoomSync",
//                fontSize = 40.sp,
//                fontWeight = FontWeight.ExtraBold,
//                fontFamily = bungeeFont,
//                color = textColor,
//                style = TextStyle(
//                    shadow = Shadow(
//                        color = accentColor.copy(alpha = 0.5f),
//                        offset = Offset(0f, 0f),
//                        blurRadius = 8f
//                    )
//                ),
//                modifier = Modifier.offset(y = (-40).dp)
//            )
                Icon(
                    painter = painterResource(id = R.drawable.jainlogo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(320.dp)
                        .padding(0.dp),
                    tint = Color(0xFFE4FFFC)
                )
            }

            // Login card with enhanced styling and shadow - updated with cardBackground
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground  // Using updated cardBackground
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF7B89).copy(alpha = 0.5f),
                            accentColor.copy(alpha = 0.5f)
                        )
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 10.dp,
                        spotColor = accentColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Faculty Login Form Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.key),
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Faculty / Admin Login",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = karlaFont,
                            color = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email field with enhanced styling - updated container colors
                    OutlinedTextField(
                        textStyle = TextStyle(
                            fontFamily = karlaFont,
                            fontSize = 18.sp,
                            color = textColor
                        ),
                        value = emailText,
                        onValueChange = { emailText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = unfocusedFieldColor,
                            focusedContainerColor = focusedFieldColor,
                            unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                            focusedBorderColor = accentColor,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor,
                            unfocusedLabelColor = textColor.copy(alpha = 0.7f),
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        ),
                        label = {
                            Text(
                                "Email / Username",
                                fontFamily = karlaFont,
                                fontSize = 15.sp,
                                color = textColor.copy(alpha = 0.8f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password field with enhanced styling - updated container colors
                    OutlinedTextField(
                        textStyle = TextStyle(
                            fontFamily = karlaFont,
                            fontSize = 18.sp,
                            color = textColor
                        ),
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = unfocusedFieldColor,
                            focusedContainerColor = focusedFieldColor,
                            unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                            focusedBorderColor = accentColor,
                            unfocusedTextColor = textColor,
                            focusedTextColor = textColor,
                            unfocusedLabelColor = textColor.copy(alpha = 0.7f),
                            focusedLabelColor = accentColor,
                            cursorColor = accentColor
                        ),
                        label = {
                            Text(
                                "Password",
                                fontFamily = karlaFont,
                                fontSize = 15.sp,
                                color = textColor.copy(alpha = 0.8f)
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    painter = if (isPasswordVisible)
                                        painterResource(id = R.drawable.visible)
                                    else
                                        painterResource(id = R.drawable.invisible),
                                    contentDescription = if (isPasswordVisible)
                                        "Hide password"
                                    else
                                        "Show password",
                                    tint = accentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (emailText.isNotBlank() && passwordText.isNotBlank()) {
                                    handleLogin(
                                        coroutineScope,
                                        client,
                                        emailText,
                                        passwordText,
                                        userViewModel,
                                        navController,
                                        onShowSnackbar,
                                        { isLoading = it }
                                    )
                                } else {
                                    onShowSnackbar("Email/Password cannot be empty!", SnackbarType.ERROR)
                                }
                            }
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Sign in button with gradient and shadow
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            if (emailText.isNotBlank() && passwordText.isNotBlank()) {
                                handleLogin(
                                    coroutineScope,
                                    client,
                                    emailText,
                                    passwordText,
                                    userViewModel,
                                    navController,
                                    onShowSnackbar,
                                    { isLoading = it }
                                )
                            } else {
                                onShowSnackbar("Email/Password cannot be empty!", SnackbarType.ERROR)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(buttonColor, accentColor)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.login),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Sign In",
                                        fontFamily = karlaFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Student access card with enhanced design - updated with backgroundColor-based color
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        spotColor = highlightColor.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = cardBackground  // Updated to match cardBackground
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF7B89).copy(alpha = 0.5f),
                            accentColor.copy(alpha = 0.2f)
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.offset(x = 50.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.school),
                            contentDescription = null,
                            tint = highlightColor,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Student Portal",
                            color = textColor,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = karlaFont
                        )
                    }

//                    Text(
//                        text = "Students can access the app directly without faculty credentials.",
//                        color = textColor,
//                        fontSize = 15.sp,
//                        lineHeight = 24.sp,
//                        fontFamily = karlaFont
//                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Student access button with animated gradient border - updated background color
                    Button(
                        onClick = {
                            userViewModel.userRole = "student"
                            userViewModel.userName = "student"
                            navController.navigate(route = "home") {
                                popUpTo(route = "starthere") {
                                    inclusive = true
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = unfocusedFieldColor,  // Updated to match field background
                            contentColor = textColor
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
//                                    accentColor.copy(alpha = 0.7f),
//                                    highlightColor.copy(alpha = 0.7f),
                                    accentColor, buttonColor
                                )
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Student Access",
                            modifier = Modifier.size(20.dp),
                            tint = highlightColor
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Enter as Student",
                            fontFamily = karlaFont,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


// Helper function to handle login logic
private fun handleLogin(
    coroutineScope: CoroutineScope,
    client: SupabaseClient,
    email: String,
    password: String,
    userViewModel: UserViewModel,
    navController: NavController,
    onShowSnackbar: (String, SnackbarType) -> Unit,
    setLoading: (Boolean) -> Unit
) {
    setLoading(true)
    coroutineScope.launch(Dispatchers.IO) {
        try {
            singInUser(client, email, password)
                .onSuccess {
                    val userId = client.auth.currentUserOrNull()?.id ?: run {
                        withContext(Dispatchers.Main) {
                            setLoading(false)
                        }
                        return@launch
                    }

                    val user = client.from("users")
                        .select(Columns.list("user_id, role, username", "profile_pic", "join_date", "favourite_quote")) {
                            filter {
                                eq("user_id", userId)
                            }
                        }.decodeSingle<LoginDataOne>()

                    val userSubs = client.from("courses")
                        .select(Columns.list("course_name")) {
                            filter {
                                eq("faculty_id", userId)
                            }
                        }.decodeList<LoginDataTwo>()

                    val courseNamesList = userSubs.map { it.course_name }

                    withContext(Dispatchers.Main) {
                        userViewModel.userId = user.user_id
                        userViewModel.userRole = user.role
                        userViewModel.userName = user.username
                        userViewModel.profilePic = user.profile_pic
                        userViewModel.joinDate = user.join_date
                        userViewModel.favouriteQuote = user.favourite_quote
                        userViewModel.course_names = courseNamesList


                        val destination = if (user.role == "admin") "adminPanel" else "home"
                        navController.navigate(route = destination) {
                            popUpTo(route = "starthere") {
                                inclusive = true
                            }
                        }
                    }
                }
                .onFailure { error ->
                    withContext(Dispatchers.Main) {
                        val message = when (error) {
                            is AuthWeakPasswordException -> "Your password is too weak!"
//                        is AuthInvalidCredentialsException -> "Invalid email or password"
//                        is AuthApiException -> "Authentication failed"
//                        is NetworkException -> "Network error. Please check your connection."
                            else -> "Please try again later."
                        }
                        onShowSnackbar("Sign-in failed: $message", SnackbarType.ERROR)
                    }
                }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onShowSnackbar("An unexpected error occurred", SnackbarType.ERROR)
            }
        } finally {
            withContext(Dispatchers.Main) {
                setLoading(false)
            }
        }
    }
}
