package com.example.herewegooo

import android.net.http.NetworkException
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
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
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.CoroutineScope


@Composable
fun Login(
    navController: NavController,
    userViewModel: UserViewModel,
    onShowSnackbar: (String, SnackbarType) -> Unit
) {
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val client = remember { supabaseClient() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        // Dynamic Background with animation
        SleekBackground()

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
            Spacer(modifier = Modifier.height(24.dp))

            // Logo placeholder - replace with your app logo
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
//                Icon(
//                    imageVector = Icons.Default.Home, //School
//                    contentDescription = "School Logo",
//                    tint = Color.White,
//                    modifier = Modifier.size(48.dp)
//                )
                Image(
                    painter = painterResource(id = R.drawable.real),
                    contentDescription = null,
                    modifier = Modifier.size(198.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RoomSync",
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = funnelFont,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Faculty & Administration Login",
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = funnelFont,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Sleek Card for login form
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1F1F24).copy(alpha = 0.85f)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Faculty Login Form
                    Text(
                        text = "Faculty Login",
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = funnelFont,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email field with sleeker design
                    OutlinedTextField(
                        textStyle = TextStyle(
                            fontFamily = karlaFont,
                            fontSize = 18.sp
                        ),
                        value = emailText,
                        onValueChange = { emailText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF282831),
                            focusedContainerColor = Color(0xFF2F2F3A),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedLabelColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color.White
                        ),
                        label = {
                            Text(
                                "Email / Username",
                                fontFamily = funnelFont,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password field with sleeker design
                    OutlinedTextField(
                        textStyle = TextStyle(
                            fontFamily = karlaFont,
                            fontSize = 18.sp
                        ),
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF282831),
                            focusedContainerColor = Color(0xFF2F2F3A),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.White.copy(alpha = 0.5f),
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            focusedLabelColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = Color.White
                        ),
                        label = {
                            Text(
                                "Password",
                                fontFamily = funnelFont,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Image(
                                    painter = if (isPasswordVisible)
                                        painterResource(id = R.drawable.visible)
                                    else
                                        painterResource(id = R.drawable.invisible),
                                    contentDescription = if (isPasswordVisible)
                                        "Hide password"
                                    else
                                        "Show password",
                                    modifier = Modifier.size(16.dp)
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Sign in button with sleeker design
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
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F6BFF),
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Sign In",
                                fontFamily = funnelFont,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Student Access Section with divider
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                    Text(
                        text = "STUDENT ACCESS",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = funnelFont,
                        fontWeight = FontWeight.Medium
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Student access button
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
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF282831),
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Student Access",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Student Access",
                        fontFamily = funnelFont,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun SleekBackground() {
    // Elegant animated background
    Box(modifier = Modifier.fillMaxSize()) {
        // Base gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121218))
        )

        // Animated particles/elements
//        val infiniteTransition = rememberInfiniteTransition(label = "background")
//        val particleAlpha by infiniteTransition.animateFloat(
//            initialValue = 0.5f,
//            targetValue = 0.8f,
//            animationSpec = infiniteRepeatable(
//                animation = tween(3000, easing = LinearEasing),
//                repeatMode = RepeatMode.Reverse
//            ),
//            label = "particles"
//        )
//
//        Canvas(modifier = Modifier.fillMaxSize()) {
//            // Draw gradient overlay
//            drawRect(
//                brush = Brush.radialGradient(
//                    colors = listOf(
//                        Color(0xFF3366FF).copy(alpha = 0.15f),
//                        Color.Transparent
//                    ),
//                    center = Offset(size.width * 0.7f, size.height * 0.3f),
//                    radius = size.minDimension * 0.8f
//                )
//            )
//
//            // Draw subtle grid pattern
//            val gridSpacing = 50.dp.toPx()
//            val gridColor = Color.White.copy(alpha = 0.03f)
//
//            // Horizontal lines
//            for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
//                drawLine(
//                    color = gridColor,
//                    start = Offset(0f, y.toFloat()),
//                    end = Offset(size.width, y.toFloat()),
//                    strokeWidth = 1f
//                )
//            }
//
//            // Vertical lines
//            for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
//                drawLine(
//                    color = gridColor,
//                    start = Offset(x.toFloat(), 0f),
//                    end = Offset(x.toFloat(), size.height),
//                    strokeWidth = 1f
//                )
//            }
//
//            // Draw accent circles/particles
//            val particlePositions = listOf(
//                Offset(size.width * 0.2f, size.height * 0.2f),
//                Offset(size.width * 0.8f, size.height * 0.15f),
//                Offset(size.width * 0.1f, size.height * 0.85f),
//                Offset(size.width * 0.9f, size.height * 0.75f),
//                Offset(size.width * 0.5f, size.height * 0.95f)
//            )
//
//            particlePositions.forEach { position ->
//                drawCircle(
//                    color = Color(0xFF4D69FF).copy(alpha = particleAlpha * 0.2f),
//                    center = position,
//                    radius = 100.dp.toPx()
//                )
//            }
//        }
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
    coroutineScope.launch {
        try {
            singInUser(client, email, password)
                .onSuccess {
                    val userId = client.auth.currentUserOrNull()?.id ?: run {
                        setLoading(false)
                        return@launch
                    }

                    val user = client.from("users")
                        .select(Columns.list("role, username")) {
                            filter {
                                eq("user_id", userId)
                            }
                        }.decodeSingle<ProfileRole>()

                    userViewModel.userRole = user.role
                    userViewModel.userName = user.username

                    val destination = if (user.role == "admin") "adminPanel" else "home"
                    navController.navigate(route = destination) {
                        popUpTo(route = "starthere") {
                            inclusive = true
                        }
                    }
                }
                .onFailure { error ->
                    val message = when (error) {
                        is AuthWeakPasswordException -> "Your password is too weak!"
//                        is AuthInvalidCredentialsException -> "Invalid email or password"
//                        is AuthApiException -> "Authentication failed"
//                        is NetworkException -> "Network error. Please check your connection."
                        else -> "Please try again later."
                    }
                    onShowSnackbar("Sign-in failed: $message", SnackbarType.ERROR)
                }
        } catch (e: Exception) {
            onShowSnackbar("An unexpected error occurred", SnackbarType.ERROR)
        } finally {
            setLoading(false)
        }
    }
}
