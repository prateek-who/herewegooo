package com.example.herewegooo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.Event
import com.example.herewegooo.network.RawEvent
import com.example.herewegooo.network.ReceiveRequests
import com.example.herewegooo.network.RequestWithFacultyName
import com.example.herewegooo.network.getFacultyName
import com.example.herewegooo.network.supabaseClient
import com.example.herewegooo.network.toEvent
import com.example.herewegooo.ui.theme.HerewegoooTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.sql.Date
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AdminPanel(
    modifier: Modifier = Modifier,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val client = supabaseClient()
    var refreshCounter by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    var lastRefreshTime by remember { mutableLongStateOf(0L) }
    var requests by remember { mutableStateOf<List<RequestWithFacultyName>>(emptyList()) }

    // Add a rotation animation state
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    val rotation = remember { Animatable(rotationAngle) }

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
                requests = getRequests(client)
                lastRefreshTime = currentTime
            } catch (e: Exception) {
                onShowSnackbar("Failed to fetch requests: ${e.localizedMessage}", SnackbarType.ERROR)
            } finally {
                isRefreshing = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121214))
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
//                                Color(0xFFD81F26),
//                                Color(0xFFFF5757))
                                Color(0xFFFFCC80),
                                Color(0xFFFFE0B2), // Pale orange-cream
                            )
                        )
                    )
                    .shadow(elevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.adminpanelsettings),
                            contentDescription = "Admin",
//                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "ADMIN PANEL",
                            color = Color.White,
                            fontFamily = oswaldFont,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = { refreshCounter++ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.2f), shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(rotation.value) // Apply the rotation here
                        )
                    }
                }
            }

            // Status bar showing pending requests count
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1D1D20))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pending Requests (${requests.size})",
                        color = Color.White,
                        fontFamily = karlaFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            // Request list
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
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF3F3F46),
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "No pending requests",
                            color = Color(0xFF9E9E9E),
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
                        RequestItem(
                            request = request,
                            onShowSnackbar = onShowSnackbar,
                            onRefresh = { refreshCounter++ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RequestItem(
    request: RequestWithFacultyName,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit,
    onRefresh: () -> Unit
) {
    var showApproveDialog by remember { mutableStateOf(false) }
    var showDenyDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    val textColorSecondary = Color(0xFFABABAF)
    val textColorPrimary = Color(0xFFF5F5F7)

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    // Format dates
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(request.request.class_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val formattedDate = parsedDate.format(dateFormatter)

    //Time shit
    val createdDateTime = LocalDateTime.parse(request.request.request_created, DateTimeFormatter.ISO_DATE_TIME)
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

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E22)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header section with date and classroom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier =
                            if(isTomorrow){
                            Modifier
                                .height(48.dp)
                                .width(80.dp)
                                .background(
                                    color =
                                        if (isToday){
                                            Color(0xFFFF0A0A)
                                        }else if (isTomorrow){
                                            Color(0xFFFF9800)
                                        }else{
                                            Color(0xFF2C2C30)
                                        },
                                    shape = RoundedCornerShape(8.dp)
                                )
                        } else {
                        Modifier
                            .size(48.dp)
                            .background(
                                color =
                                    if (isToday) {
                                        Color(0xFFFF0A0A)
                                    } else if (isTomorrow) {
                                        Color(0xFFFF9800)
                                    } else {
                                        Color(0xFF2C2C30)
                                    },
                                shape = RoundedCornerShape(8.dp)
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
                        if (isTomorrow) {
                            Text(
                                text = "${request.request.start_time.format(timeFormatter)} - ${
                                    request.request.end_time.format(
                                        timeFormatter
                                    )
                                }",
                                color = textColorPrimary,
                                fontFamily = karlaFont,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }else{
                            Text(
                                text = "${request.request.start_time.format(timeFormatter)} - ${
                                    request.request.end_time.format(
                                        timeFormatter
                                    )
                                }",
                                color = textColorPrimary,
                                fontFamily = karlaFont,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Classroom ${request.request.classroom_id}",
                            color = textColorSecondary,
                            fontFamily = karlaFont,
                            fontSize = 14.sp
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(0xFF2C2C30),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = if (expanded) "Show less" else "Show more",
                        tint = textColorSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Faculty name with icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = textColorSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = request.facultyName,
                    color = textColorPrimary,
                    fontFamily = karlaFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Expandable content
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = Color(0xFF2C2C30), thickness = 1.dp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reason
                    Column {
                        Text(
                            text = "REASON",
                            color = textColorSecondary,
                            fontFamily = oswaldFont,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = request.request.reason,
                            color = textColorPrimary,
                            fontFamily = karlaFont,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Request timestamp
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.schedule),
                            contentDescription = null,
//                            tint = textColorSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        println(timeFormatter)
                        Text(
                            text = "Requested on $formattedCreatedDate at $formattedCreatedTime",
                            color = textColorSecondary,
                            fontFamily = karlaFont,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showDenyDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C2C30),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color(0xFFFF453A),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Deny",
                            fontFamily = karlaFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Button(
                    onClick = { showApproveDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF30D158),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Approve",
                            fontFamily = karlaFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Dialogs
            if (showApproveDialog) {
                AdminDialogue(
                    openDialog = true,
                    onDismiss = {
                        showApproveDialog = false
                        onRefresh()
                    },
                    bookingDate = request.request.class_date,
                    roomNumber = request.request.classroom_id.toString(),
                    fromTime = request.request.start_time,
                    toTime = request.request.end_time,
                    facultyId = request.request.faculty_id,
                    facultyName = request.facultyName,
                    onShowSnackbar = onShowSnackbar
                )
            }

            if (showDenyDialog) {
                AdminDenyDialogue(
                    openDialog = true,
                    onDismiss = {
                        showDenyDialog = false
                        onRefresh()
                    },
                    bookingDate = request.request.class_date,
                    roomNumber = request.request.classroom_id.toString(),
                    fromTime = request.request.start_time,
                    toTime = request.request.end_time,
                    facultyId = request.request.faculty_id,
                    facultyName = request.facultyName,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
    }
}


suspend fun getNames(
    client: SupabaseClient,
    facultyId: String
): String{
    val name = client.from("users").select(columns = Columns.list("username")){
        filter {
            eq("user_id", facultyId)
        }
    }.decodeSingle<getFacultyName>()

    return name.username
}


suspend fun getRequests(client: SupabaseClient): List<RequestWithFacultyName> {
    val timeRightNow = LocalTime.now()
    val dateRightNow = LocalDate.now()

    val rawList = client.from("requests").select(columns = Columns.ALL).decodeList<ReceiveRequests>()

    val futureRequests = rawList.filter { request ->
        val requestedDate = LocalDate.parse(request.class_date)

        when{
            requestedDate.isAfter(dateRightNow) -> true
            requestedDate.isEqual(dateRightNow) -> request.start_time.isAfter(timeRightNow)
            else -> false
        }
    }

    println(futureRequests)
    return futureRequests.map { request ->
        RequestWithFacultyName(
            request = request,
            facultyName = getNames(client, request.faculty_id)
        )
    }
}