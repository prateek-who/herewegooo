package com.example.herewegooo

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.util.CoilUtils
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.net.URI
import java.net.URLEncoder
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.exp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelfProfile(navController: NavController, userViewModel: UserViewModel) {
    // Your original background color
    val backgroundColor = Color(0xFF121218)
    // Text color
    val textColor = Color(0xFFF0F0F5)
    // Accent color - one of the pastel colors
    val accentColor = Color(0xFF9676DB)
    // Red color for signout
    val signOutColor = Color(0xFFDB7676)

    val subjectList = listOf("Computer Network", "Computer Forensics", "Fundamentals of Information Security", "Data Structures and Algorithms")
    val classCount = 5
    val favoutireQuote = "The only way to do great work is to love what you do. - Steve Jobs"

    val client = supabaseClient()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val experience = experinceCalculator(user = userViewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Top section with profile picture
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Profile picture with border
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .border(width = 3.dp, color = accentColor, shape = CircleShape)
                    .align(Alignment.BottomCenter)
            ) {
                val imageLoader = ImageLoader.Builder(context)
                    .components{
                        add(
                            OkHttpNetworkFetcherFactory(
                                callFactory = {
                                    OkHttpClient.Builder()
                                        .followRedirects(true)
                                        .build()
                                }
                            )
                        )
                    }
                    .build()

                AsyncImage(
                    model = userViewModel.profilePic,
                    imageLoader = imageLoader,
                    contentDescription = "Profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = painterResource(id = R.drawable.default_profil)
                )

                // Camera icon for updating profile pic
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .border(width = 2.dp, color = backgroundColor, shape = CircleShape)
                        .align(Alignment.BottomEnd)
//                        .clickable { /* Handle click */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change profile picture",
                        tint = textColor,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }

        // Name and Role
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = userViewModel.userName,
                fontFamily = karlaFont,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(top = 10.dp)
            )

            Text(
                text = userViewModel.userRole.replaceFirstChar{ it.uppercase() },
                fontFamily = karlaFont,
                fontSize = 16.sp,
                color = textColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Middle Insane Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E26)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(title = "Classes Today", value = classCount.toString(), textColor = textColor)

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )

                StatItem(title = "Subjects", value = subjectList.size.toString(), textColor = textColor)

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )

                StatItem(title = "Experience", value = if (experience.years > 0) "${experience.years}y ${experience.months}m" else "${experience.months}m", textColor = textColor)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Subjects
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Subjects",
                fontFamily = karlaFont,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val pastelColors = listOf(
                    Color(0xFF6A98DB), // Medium blue pastel
                    Color(0xFF9676DB), // Medium purple pastel
                    Color(0xFFDB76A6), // Medium rose pastel
                    Color(0xFF76DBA6), // Medium mint pastel
                    Color(0xFFDBA676), // Medium tan pastel
                    Color(0xFF96DB76)  // Medium lime pastel
                )

                subjectList.forEachIndexed { index, subject ->
                    SubjectChip(
                        subject = subject,
                        color = pastelColors[index % pastelColors.size]
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Favorite Quote
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = accentColor.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Favorite Quote",
                        color = accentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = karlaFont
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = favoutireQuote,
                    color = textColor,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    fontFamily = karlaFont
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Cool Sign Out Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable {
                    coroutineScope.launch {
                        try {
                            signoutUser(client)

                            userViewModel.resetUserState()
                            // Navigate to login screen after successful logout
                            navController.navigate("starthere") {
                                popUpTo(0) { inclusive = true }
                            }
                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Failed to sign out: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E1E26)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = signOutColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Sign Out",
                    color = signOutColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = karlaFont
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun StatItem(title: String, value: String, textColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = karlaFont
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = title,
            color = textColor.copy(alpha = 0.7f),
            fontSize = 14.sp,
            fontFamily = karlaFont
        )
    }
}

@Composable
fun SubjectChip(subject: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color
    ) {
        Text(
            text = subject,
            color = Color(0xFF2D2D3A),
            fontWeight = FontWeight.Medium,
            fontFamily = karlaFont,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

fun experinceCalculator(user: UserViewModel): Period {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val joinDate = LocalDate.parse(user.joinDate, formatter)
    val currentDate = LocalDate.now()

    val experience = Period.between(joinDate, currentDate)

    println(experience)

    return experience
}

suspend fun signoutUser(client: SupabaseClient){
    client.auth.signOut(SignOutScope.GLOBAL)
}

// Example of what UserViewModel might look like
//class UserViewModel : ViewModel() {
//    val userName = "Sarah Johnson"
//    val profilePicUrl = "https://example.com/profile.jpg"
//    val role = "Science Department Head"
//    val classCount = 4
//    val subjects = listOf("Physics", "Chemistry", "Advanced Mathematics", "Astronomy")
//    val favoriteQuote = "The important thing is to never stop questioning. - Albert Einstein"
//}