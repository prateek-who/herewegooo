package com.example.herewegooo


import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.example.herewegooo.network.FacultyRequestHistoryItem
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.exp


@Composable
fun RequestHistory(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val client = supabaseClient()
    var refreshCounter by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    var lastRefreshTime by remember { mutableLongStateOf(0L) }
    var requests by remember { mutableStateOf<List<FacultyRequestHistoryItem>>(emptyList()) }
    val facultyId = userViewModel.userId

    // Add a rotation animation state
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(rotationAngle) }

    // Updated color scheme to match Login screen
    val backgroundColor = Color(0xFF1E1E2E)  // Deep blue background
    val textColor = Color(0xFFF8F8FF)         // Bright white text
    val accentColor = Color(0xFF9676DB)       // Bright blue accent
    val buttonColor = Color(0xFF16B1AC)       // Teal button color
    val cardBackground = Color(0xFF272C3D)    // Slightly brighter card background
    val highlightColor = Color(0xFF72F2EB)    // Highlight color

    LaunchedEffect(refreshCounter) {
        // Start the rotation animation
        rotation.animateTo(
            targetValue = rotationAngle + 360f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
        // Update the base rotation angle after animation completes
        rotationAngle += 360f

        val currentTime = System.currentTimeMillis()
        // Only refresh if at least 30 seconds have passed since last refresh
        if (currentTime - lastRefreshTime > 30000 || lastRefreshTime == 0L) {
            isRefreshing = true
            try {
                // Update lastRefreshTime
                requests = getRequestHistory(client, facultyId)
                lastRefreshTime = currentTime
            } catch (e: Exception) {
                onShowSnackbar("Failed to fetch request history: ${e.localizedMessage}", SnackbarType.ERROR)
            } finally {
                isRefreshing = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern header with gradient background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                buttonColor,  // Teal color from login
                                accentColor   // Purple accent from login
                            )
                        )
                    )
                    .shadow(elevation = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Improved header layout
                    Text(
                        text = "Request History",
                        color = textColor,
                        fontFamily = oswaldFont,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    // Refresh button with updated styling
                    IconButton(
                        onClick = { refreshCounter++ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = textColor,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotation.value)
                        )
                    }
                }
            }

            // Status bar showing request count with updated styling
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(cardBackground)
//                    .padding(horizontal = 24.dp, vertical = 12.dp)
//                    .shadow(4.dp)
//            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        Icon(
//                            painter = painterResource(R.drawable.history),
//                            contentDescription = "History",
//                            tint = accentColor,
//                            modifier = Modifier.size(20.dp)
//                        )
//                        Text(
//                            text = "All Requests (${requests.size})",
//                            color = textColor,
//                            fontFamily = karlaFont,
//                            fontSize = 16.sp,
//                            fontWeight = FontWeight.Medium
//                        )
//                    }
//
//                    if (isRefreshing) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.size(20.dp),
//                            color = highlightColor,
//                            strokeWidth = 2.dp
//                        )
//                    }
//                }
//            }

            // Request list - updated empty state
            if (requests.isEmpty() && !isRefreshing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.history),
                            contentDescription = null,
                            tint = accentColor.copy(alpha = 0.6f),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No request history",
                            color = textColor.copy(alpha = 0.7f),
                            fontFamily = karlaFont,
                            fontSize = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(requests) { request ->
                        RequestHistoryItem(
                            request = request,
                            cardBackground = cardBackground,
                            textColor = textColor,
                            accentColor = accentColor,
                            highlightColor = highlightColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestHistoryItem(
    request: FacultyRequestHistoryItem,
    cardBackground: Color,
    textColor: Color,
    accentColor: Color,
    highlightColor: Color
) {
    var expanded by remember { mutableStateOf(false) }

    val textColorSecondary = textColor.copy(alpha = 0.7f)

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Format dates
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(request.class_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val formattedDate = parsedDate.format(dateFormatter)

    //Time operations
    val createdDateTime = LocalDateTime.parse(request.request_created, DateTimeFormatter.ISO_DATE_TIME)
    val istZone = ZoneId.of("Asia/Kolkata")
    val utcZone = ZoneId.of("UTC")

    val createdZonedDateTime = createdDateTime.atZone(utcZone).withZoneSameInstant(istZone)

    //Final conversion of date and time
    val formattedCreatedDate = createdZonedDateTime.format(dateFormatter)
    val formattedCreatedTime = createdZonedDateTime.format(timeFormatter)

    val isToday = parsedDate.isEqual(LocalDate.now())
    val isTomorrow = parsedDate.isEqual(LocalDate.now().plusDays(1))
    val dateLabel = when {
        isToday -> "Today"
        isTomorrow -> "Tomorrow"
        else -> formattedDate
    }

    // Status colors updated to match new color scheme
    val statusColor = when (request.status) {
        "approved" -> Color(0xFF30D158)
        "denied" -> Color(0xFFFF453A)
        else -> Color(0xFFFF9800) // Pending
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = accentColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    accentColor.copy(alpha = 0.3f),
                    highlightColor.copy(alpha = 0.3f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status indicator at the top
            Row(
                modifier = Modifier.fillMaxWidth()
//                    .background(Color.Black)
                ,
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status chip with updated styling
                Box(
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.8f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = when (request.status) {
                                "approved" -> painterResource(R.drawable.checkcircle)
                                "denied" -> painterResource(R.drawable.close)
                                else -> painterResource(R.drawable.hourglass)
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = request.status,
                            color = Color.White,
                            fontFamily = karlaFont,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Expand button with updated styling
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = accentColor.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = if (expanded) "Show less" else "Show more",
                        tint = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Header section with date and classroom - updated styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Updated date display box with gradients
                Box(
                    modifier =
                        if(isTomorrow) {
                            Modifier
                                .height(48.dp)
                                .width(80.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            if (isToday) Color(0xFFFF0A0A)
                                            else if (isTomorrow) Color(0xFFFF9800)
                                            else accentColor,
                                            if (isToday) Color(0xFFFF453A).copy(alpha = 0.8f)
                                            else if (isTomorrow) Color(0xFFFF9800).copy(alpha = 0.8f)
                                            else accentColor.copy(alpha = 0.7f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        } else {
                            Modifier
                                .size(48.dp)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            if (isToday) Color(0xFFFF0A0A)
                                            else if (isTomorrow) Color(0xFFFF9800)
                                            else accentColor,
                                            if (isToday) Color(0xFFFF453A).copy(alpha = 0.8f)
                                            else if (isTomorrow) Color(0xFFFF9800).copy(alpha = 0.8f)
                                            else accentColor.copy(alpha = 0.7f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isToday || isTomorrow) dateLabel else parsedDate.dayOfMonth.toString(),
                            color = Color.White,
                            fontFamily = karlaFont,
                            fontSize = if (isToday || isTomorrow) 16.sp else 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (!isToday && !isTomorrow) {
                            Text(
                                text = parsedDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                                color = Color.White,
                                fontFamily = karlaFont,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${request.start_time.format(timeFormatter)} - ${
                            request.end_time.format(
                                timeFormatter
                            )
                        }",
                        color = textColor,
                        fontFamily = karlaFont,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Classroom ${request.classroom_id}",
                        color = highlightColor,
                        fontFamily = karlaFont,
                        fontSize = 14.sp
                    )
                }
            }

            // Expandable content with updated styling
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(
                        color = accentColor.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reason with updated styling
                    Column {
                        Text(
                            text = "REASON",
                            color = accentColor,
                            fontFamily = oswaldFont,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = request.reason,
                            color = textColor,
                            fontFamily = karlaFont,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Request timestamp with updated styling
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.schedule),
                            contentDescription = null,
                            tint = highlightColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Requested on $formattedCreatedDate at $formattedCreatedTime",
                            color = textColorSecondary,
                            fontFamily = karlaFont,
                            fontSize = 14.sp
                        )
                    }

                    if (request.status != "pending") {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Response timestamp with updated icons and colors
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = if (request.status == "approved") painterResource(R.drawable.checkcircle) else painterResource(id = R.drawable.close),
                                contentDescription = null,
                                tint = if (request.status == "approved") Color(0xFF30D158) else Color(0xFFFF453A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (request.status == "approved") "Request approved" else "Request denied",
                                color = if (request.status == "approved") Color(0xFF30D158) else Color(0xFFFF453A),
                                fontFamily = karlaFont,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Admin comment with updated styling
                        if (request.admin_comment != null && request.admin_comment.isNotEmpty() && request.status != "approved") {
                            Spacer(modifier = Modifier.height(16.dp))
                            Column {
                                Text(
                                    text = "ADMIN COMMENT",
                                    color = accentColor,
                                    fontFamily = oswaldFont,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = accentColor.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = request.admin_comment,
                                        color = textColor,
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


suspend fun getRequestHistory(
    client: SupabaseClient,
    facultyId: String
): List<FacultyRequestHistoryItem> {
    try {
        val rawList = client.from("requests")
            .select(columns = Columns.list("class_date, start_time, end_time, classroom_id, request_created, reason, status, admin_comment")) {
                filter {
                    eq("faculty_id", facultyId)
                }
                order("class_date", Order.DESCENDING)
            }.decodeList<FacultyRequestHistoryItem>()

        println(rawList)
        return rawList
    } catch (e: Exception) {
        println("Error fetching request history: $e")
        return emptyList()
    }
}