package com.example.herewegooo


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.GetFacultyName
import com.example.herewegooo.network.SwapReceiveRequest
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun SwapHistory(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit,
) {
    val client = supabaseClient()
    var refreshCounter by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    var lastRefreshTime by remember { mutableLongStateOf(0L) }
    var swapRequests by remember { mutableStateOf<List<SwapReceiveRequest>>(emptyList()) }
    val teacherId = userViewModel.userId

    val coroutineScope = rememberCoroutineScope()

    // Track view type (All, Incoming, Outgoing)
    var viewType by remember { mutableStateOf(SwapViewType.ALL) }

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
                swapRequests = getSwapRequests(client, teacherId)
                lastRefreshTime = currentTime
            } catch (e: Exception) {
                onShowSnackbar("Failed to fetch swap requests: ${e.localizedMessage}", SnackbarType.ERROR)
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
                        text = "Swap Requests",
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

            // Filter tabs for All/Incoming/Outgoing
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground)
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterTab(
                        text = "All",
                        selected = viewType == SwapViewType.ALL,
                        onClick = { viewType = SwapViewType.ALL },
                        accentColor = accentColor,
                        textColor = textColor
                    )
                    FilterTab(
                        text = "Incoming",
                        selected = viewType == SwapViewType.INCOMING,
                        onClick = { viewType = SwapViewType.INCOMING },
                        accentColor = accentColor,
                        textColor = textColor
                    )
                    FilterTab(
                        text = "Outgoing",
                        selected = viewType == SwapViewType.OUTGOING,
                        onClick = { viewType = SwapViewType.OUTGOING },
                        accentColor = accentColor,
                        textColor = textColor
                    )
                }
            }

            // Status bar showing request count with updated styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(cardBackground.copy(alpha = 0.7f))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
//                    .shadow(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.history),
                            contentDescription = "History",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = when (viewType) {
                                SwapViewType.ALL -> "All Request(s) (${swapRequests.size})"
                                SwapViewType.INCOMING -> "Incoming Swap Request(s) (${swapRequests.count { it.to_id == teacherId }})"
                                SwapViewType.OUTGOING -> "Outgoing Swap Request(s) (${swapRequests.count { it.from_id == teacherId }})"
                            },
                            color = textColor,
                            fontFamily = karlaFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = highlightColor,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            // Filtered requests
            val filteredRequests = when (viewType) {
                SwapViewType.ALL -> swapRequests
                SwapViewType.INCOMING -> swapRequests.filter { it.to_id == teacherId }
                SwapViewType.OUTGOING -> swapRequests.filter { it.from_id == teacherId }
            }

            // Empty state
            if (filteredRequests.isEmpty() && !isRefreshing) {
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
                            text = when (viewType) {
                                SwapViewType.ALL -> "No swap requests found"
                                SwapViewType.INCOMING -> "No incoming swap requests"
                                SwapViewType.OUTGOING -> "No outgoing swap requests"
                            },
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
                    items(filteredRequests) { request ->
                        SwapHistoryItem(
                            request = request,
                            currentUserId = teacherId,
                            cardBackground = cardBackground,
                            textColor = textColor,
                            accentColor = accentColor,
                            highlightColor = highlightColor,
                            onShowSnackbar = onShowSnackbar,
                            onDeclineClicked = { swapRequest ->
                                // Decline the swap request
                                coroutineScope.launch {
                                    denySwapRequestStatus(
                                        client,
                                        swapRequest.id.toString(),
                                        "declined"
                                    ) { success ->
                                        if (success) {
                                            refreshCounter++
                                            onShowSnackbar(
                                                "Swap request declined",
                                                SnackbarType.SUCCESS
                                            )
                                        } else {
                                            onShowSnackbar(
                                                "Failed to decline swap request",
                                                SnackbarType.ERROR
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    accentColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) accentColor.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) accentColor else textColor.copy(alpha = 0.7f),
            fontFamily = karlaFont,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun SwapHistoryItem(
    request: SwapReceiveRequest,
    currentUserId: String,
    cardBackground: Color,
    textColor: Color,
    accentColor: Color,
    highlightColor: Color,
    onShowSnackbar: (String, SnackbarType) -> Unit,
    onDeclineClicked: (SwapReceiveRequest) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var fromTeacherName by remember { mutableStateOf("Loading...") }
    var toTeacherName by remember { mutableStateOf("Loading...") }
    val client = supabaseClient()

    val textColorSecondary = textColor.copy(alpha = 0.7f)
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    var onApproveDialog by remember { mutableStateOf(false) }


    // Format dates
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(request.class_date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val formattedDate = parsedDate.format(dateFormatter)

    //Time operations
    val createdDateTime = LocalDateTime.parse(request.created_at, DateTimeFormatter.ISO_DATE_TIME)
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

    // Is this an incoming request for the current user?
    val isIncoming = request.to_id == currentUserId

    // Show action buttons only for pending incoming requests
    val showActions = isIncoming && request.status == "pending"

    // Status colors updated to match new color scheme
    val statusColor = when (request.status) {
        "approved" -> Color(0xFF30D158)
        "declined" -> Color(0xFFFF453A)
        else -> Color(0xFFFF9800) // Pending
    }

    // Fetch teacher names
    LaunchedEffect(request) {
        fromTeacherName = getUserName(client, request.from_id) ?: "Unknown"
        toTeacherName = getUserName(client, request.to_id) ?: "Unknown"
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
            // Request type and status indicator at the top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Direction indicator
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isIncoming) accentColor.copy(alpha = 0.2f) else highlightColor.copy(
                                alpha = 0.2f
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            painter = if (isIncoming) painterResource(id = R.drawable.incoming) else painterResource(id = R.drawable.outgoing),
                            contentDescription = null,
                            tint = if (isIncoming) accentColor else highlightColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isIncoming) "Incoming" else "Outgoing",
                            color = if (isIncoming) accentColor else highlightColor,
                            fontFamily = karlaFont,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Status chip
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
                                "declined" -> painterResource(R.drawable.close)
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Header section with date and classroom
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date display box with gradients
                Box(
                    modifier = if(isTomorrow) {
                        Modifier
                            .height(48.dp)
                            .width(80.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        if (isToday && request.status == "pending") Color(0xFFFF0A0A)
                                        else if (isTomorrow && request.status == "pending") Color(0xFFFF9800)
                                        else accentColor,
                                        if (isToday && request.status == "pending") Color(0xFFFF453A).copy(alpha = 0.5f)
                                        else if (isTomorrow && request.status == "pending") Color(0xFFFF9800).copy(alpha = 0.5f)
                                        else accentColor.copy(alpha = 0.5f)
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
                                        if (isToday && request.status == "pending") Color(0xFFFF0A0A)
                                        else if (isTomorrow && request.status == "pending") Color(0xFFFF9800)
                                        else accentColor,
                                        if (isToday && request.status == "pending") Color(0xFFFF453A).copy(alpha = 0.5f)
                                        else if (isTomorrow && request.status == "pending") Color(0xFFFF9800).copy(alpha = 0.5f)
                                        else accentColor.copy(alpha = 0.5f)
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

                Spacer(modifier = Modifier.weight(1f))

                // Expand button
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

            // Actions for pending incoming requests
            if (showActions) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Decline button
                    Button(
                        onClick = { onDeclineClicked(request) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF453A).copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Decline",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Decline",
                            color = Color.White,
                            fontFamily = karlaFont,
                            fontSize = 14.sp
                        )
                    }

                    // Accept button
                    Button(
                        onClick = { onApproveDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF30D158).copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.checkcircle),
                            contentDescription = "Accept",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Accept Swap",
                            color = Color.White,
                            fontFamily = karlaFont,
                            fontSize = 14.sp
                        )
                    }

                    if (onApproveDialog) {
                        SwapAcceptDialogue(
                            openDialog = true,
                            onDismiss = {
                                onApproveDialog = false
//                                onRefresh()
                            },
                            bookingDate = request.class_date,
                            roomNumber = request.classroom_id.toString(),
                            fromTime = request.start_time,
                            toTime = request.end_time,
                            toFacultyId = request.to_id,
                            fromFacultyId = request.from_id,
                            onShowSnackbar = onShowSnackbar
                        )
                    }
                }
            }

            // Expandable content
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(
                        color = accentColor.copy(alpha = 0.3f),
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Teacher information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (isIncoming) {
                            // From teacher gets from?
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FROM",
                                    color = accentColor,
                                    fontFamily = oswaldFont,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = fromTeacherName,
                                    color = textColor,
                                    fontFamily = karlaFont,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }else {
                            // To teacher gets to?
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TO",
                                    color = accentColor,
                                    fontFamily = oswaldFont,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = toTeacherName,
                                    color = textColor,
                                    fontFamily = karlaFont,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reason
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

                    // Request timestamp
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
                        // Status information
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = if (request.status == "approved") painterResource(R.drawable.checkcircle) else painterResource(id = R.drawable.close),
                                contentDescription = null,
                                tint = if (request.status == "approved") Color(0xFF30D158) else Color(0xFFFF453A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (request.status == "approved") "Swap approved" else "Swap declined",
                                color = if (request.status == "approved") Color(0xFF30D158) else Color(0xFFFF453A),
                                fontFamily = karlaFont,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}



// Helper functions
suspend fun getSwapRequests(
    client: SupabaseClient,
    teacherId: String
): List<SwapReceiveRequest> {
    try {
        // Get all swap requests where teacher is either sender or receiver
        val rawList = client.from("swaprequest")
            .select(columns = Columns.list("id, from_id, to_id, class_date, start_time, end_time, classroom_id, reason, status, created_at")) {
                filter {
                    or {
                        eq("from_id", teacherId)
                        eq("to_id", teacherId)
                    }
                }
                order("class_date", Order.DESCENDING)
                order("created_at", Order.DESCENDING)
            }.decodeList<SwapReceiveRequest>()
        return rawList

    } catch (e: Exception) {
        println("Error fetching swap requests: $e")
        return emptyList()
    }
}

suspend fun getUserName(client: SupabaseClient, userId: String): String? {
    return try {
        val userResponse = client.from("users")
            .select(columns = Columns.list("username")) {
                filter {
                    eq("user_id", userId)
                }
            }.decodeSingle<GetFacultyName>()
        userResponse.username
    } catch (e: Exception) {
        println("Error fetching user name for $userId: $e")
        null
    }
}

suspend fun denySwapRequestStatus(
    client: SupabaseClient,
    requestId: String,
    status: String,
    callback: (Boolean) -> Unit
) {
    try {
        client.from("swaprequest").update(
            {
                set("status", status)
            }
        ){
            filter {
                eq("id", requestId)
            }
        }
        callback(true)
    } catch (e: Exception) {
        println("Error updating swap request status: $e")
        callback(false)
    }
}


// Data models
enum class SwapViewType {
    ALL, INCOMING, OUTGOING
}
