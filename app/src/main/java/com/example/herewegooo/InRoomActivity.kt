package com.example.herewegooo

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.Event
import com.example.herewegooo.network.RawEvent
import com.example.herewegooo.network.supabaseClient
import com.example.herewegooo.network.toEvent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import java.sql.Date
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun WeInRoom(
    floor: String,
    room: String,
    userViewModel: UserViewModel,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    // Time configuration
    val startHour = 7
    val endHour = 20
    val pixelsPerHour = 180.dp
    val totalHours = endHour - startHour

    // State management
    val client = supabaseClient()
    val currentDate = LocalDate.now()
    var showDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    // Background and accent colors
    val backgroundColor = Color(0xFF1E1E2E)
    val headerColor = Color(0xFF2A2A3C)
    val accentColor = Color(0xFF9676DB)
    val dropDownListColor = Color(0xFF3A3658)
    val buttonColor = Color(0xFF4A3ABA)
    val textColor = Color.White

    // Date options setup
    val todayKey = currentDate.format(DateTimeFormatter.ISO_DATE)
    val dateOptions = remember(todayKey) {
        (0 until 10).map { offset ->
            LocalDate.now()
                .plusDays(offset.toLong())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }

    var selectedDate by remember { mutableStateOf(dateOptions.firstOrNull() ?: "Select Date") }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }

    // Load events when date changes
    LaunchedEffect(selectedDate) {
        val localDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val newFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val newlocalDate = localDate.format(newFormatter)
        val sqlDate = Date.valueOf(newlocalDate)
        events = generateEvent(client, sqlDate, room)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with room and floor info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = headerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            if (userViewModel.userRole == "teacher" || userViewModel.userRole == "admin") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location information group
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Floor number
                        Column(
                            modifier = Modifier.offset(x = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Floor",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = funnelFont,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = floor,
                                    color = textColor,
                                    fontFamily = funnelFont,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Room number
                        Column(
                            modifier = Modifier.offset(x = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Room",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = funnelFont,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = room,
                                    color = textColor,
                                    fontFamily = funnelFont,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Book slot button (only for teachers and admins)
                    Button(
                        onClick = { showDialog = true },
                        modifier = Modifier
                            .height(46.dp)
                            .padding(start = 16.dp)
                            .offset(y = 11.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Book slot",
                                tint = textColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Book Slot",
                                fontFamily = funnelFont,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(headerColor)
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location information
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Current Location",
                            color = textColor.copy(alpha = 0.6f),
                            fontFamily = funnelFont,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Jain University",
                            color = textColor,
                            fontFamily = funnelFont,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Location indicators container
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Floor number
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Floor",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = funnelFont,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = floor,
                                    color = textColor,
                                    fontFamily = funnelFont,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Room number
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Room",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = funnelFont,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .width(76.dp)
                                    .height(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = room,
                                    color = textColor,
                                    fontFamily = funnelFont,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Date selector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 0.dp)
                .offset(y = (-2).dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = headerColor.copy(alpha = 0.8f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Date",
                    color = textColor,
                    fontFamily = funnelFont,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor)
                            .clickable { expanded = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                .format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")),
                            color = textColor,
                            fontFamily = funnelFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select date",
                            tint = textColor
                        )
                    }

                    Box(modifier = Modifier.offset(x = 10.dp, y = 47.dp)) {
                        DropdownMenu(
                            modifier = Modifier
                                .background(dropDownListColor)
                                .align(Alignment.Center),
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            val itemHeight = 48.dp // Approximate height of DropdownMenuItem
                            val maxHeight = 250.dp
                            val totalHeight =
                                (dateOptions.size * itemHeight).coerceAtMost(maxHeight)

                            Box(
                                modifier = Modifier
                                    .height(totalHeight)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Column (modifier = Modifier.width(270.dp)){
                                    dateOptions.forEach { date ->
                                        val formattedDisplayDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                            .format(DateTimeFormatter.ofPattern("EEE, dd MMM"))
                                        DropdownMenuItem(
                                            onClick = {
                                                selectedDate = date
                                                expanded = false
                                            },
                                            text = {
                                                Text(
                                                    text = formattedDisplayDate,
                                                    color = Color.White,
                                                    fontFamily = funnelFont,
                                                    fontSize = if (date == selectedDate) 20.sp else 16.sp,
                                                    fontWeight = if (date == selectedDate) FontWeight.Bold else FontWeight.Normal,
                                                    modifier = Modifier.offset(x = 10.dp)
                                                )
                                            },
                                            modifier = Modifier.background(
                                                if (date == selectedDate) accentColor.copy(alpha = 0.2f) else Color.Transparent
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Schedule view
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .offset(y = (-4).dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = headerColor.copy(alpha = 0.6f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((totalHours * pixelsPerHour.value).dp)
                    .padding(vertical = 8.dp)
            ) {
                TimelineComponent(
                    modifier = Modifier
                        .width(80.dp)
                        .fillMaxHeight(),
                    startHour = startHour,
                    endHour = endHour,
                    pixelsPerHour = pixelsPerHour,
                )

                EventsComponent(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    events = events,
                    startHour = startHour,
                    pixelsPerHour = pixelsPerHour
                )
            }
        }
    }

    // Booking dialog
    if (showDialog) {
        BookingDialog(
            openDialog = true,
            onDismiss = { showDialog = false },
            roomNumber = room,
            user = userViewModel,
            onShowSnackbar = onShowSnackbar
        )
    }
}

@Composable
fun TimelineComponent(
    modifier: Modifier = Modifier,
    startHour: Int,
    endHour: Int,
    pixelsPerHour: Dp,
) {
    val lineColor = Color(0xFF4D4D56)
    val hourLineColor = Color(0xFF4D4D56)
    val halfHourLineColor = Color(0xFF3A3A42).copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Column {
            (startHour until endHour).forEach { hour ->
                Box(
                    modifier = Modifier
                        .height(pixelsPerHour)
                        .fillMaxWidth()
                ) {
                    // Full Hour indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time label with enhanced styling
                        Box(
                            modifier = Modifier
                                .width(75.dp)
                                .padding(start = 8.dp, end = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = LocalTime.of(hour, 0)
                                    .format(DateTimeFormatter.ofPattern("h:mm a")),
                                fontFamily = oswaldFont,
                                fontSize = 16.sp,
                                color = Color(0xFFA2A2A8),
                                letterSpacing = 0.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            //Full Hour line
                            HorizontalDivider(
                                modifier = Modifier
                                    .width(19.dp)
                                    .offset(x = 52.dp),
                                color = lineColor
                            )
                        }

                        // Horizontal divider with subtle drop shadow
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .shadow(
                                    elevation = 1.dp,
                                    shape = RoundedCornerShape(0.5.dp),
                                    spotColor = hourLineColor
                                )
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            hourLineColor.copy(alpha = 0.9f),
                                            hourLineColor.copy(alpha = 0.7f)
                                        )
                                    )
                                )
                        )
                    }

                    // Half Hour indicator - more subtle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = pixelsPerHour / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Half-hour label with lighter styling
                        Box(
                            modifier = Modifier
                                .width(75.dp)
                                .padding(start = 8.dp, end = 4.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = LocalTime.of(hour, 30)
                                    .format(DateTimeFormatter.ofPattern("h:mm a")),
                                fontFamily = oswaldFont,
                                fontSize = 12.sp,
                                color = Color(0xFFA2A2A8).copy(alpha = 0.8f),
                                letterSpacing = 0.2.sp,
                                fontWeight = FontWeight.Normal
                            )

                            //Fuck that we dividin' here (Half Hour line)
                            HorizontalDivider(
                                modifier = Modifier
                                    .width(30.dp)
                                    .offset(x = 41.dp),
                                color = lineColor
                            )
                        }
                    }
                }
            }
        }

        // Vertical gradient overlay to add depth at the edges
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(8.dp)
                .align(Alignment.CenterStart)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF252530).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}


@Composable
fun EventsComponent(
    modifier: Modifier = Modifier,
    events: List<Event>,
    startHour: Int,
    pixelsPerHour: Dp
) {
    Box(modifier = modifier) {
        // Timeline indicator - refined appearance
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(2.dp)
                .offset(x = 3.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Gray.copy(alpha = 0.1f),
                            Color.Gray.copy(alpha = 0.3f),
                            Color.Gray.copy(alpha = 0.1f)
                        )
                    )
                )
        )

        events.forEach { event ->
            val startMinutes = event.startTime.toSecondOfDay() / 60
            val minutesFromStart = startMinutes - (startHour * 60)
            val durationInMinutes = Duration.between(event.startTime, event.endTime).toMinutes()
            val durationHours = durationInMinutes / 60f
            val offsetY = (minutesFromStart / 60f * pixelsPerHour.value).dp

            val startTimeFormat = event.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            val endTimeFormat = event.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))

            val cardElevation = animateDpAsState(
                targetValue = 4.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .offset(x = 3.dp)
            ) {
                // Event indicator line with rounded ends
                Box(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .height((durationHours * pixelsPerHour.value).dp - 2.dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(event.color)
                )

                // Event card with enhanced styling and animations
                val cornerSize = 16.dp
                Box(
                    modifier = Modifier
                        .offset(y = offsetY, x = 4.dp)
                        .height((durationHours * pixelsPerHour.value).dp - 2.dp)
                        .width(264.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 5.dp,
                                topEnd = cornerSize,
                                bottomStart = 5.dp,
                                bottomEnd = cornerSize
                            )
                        )
                        .shadow(
                            elevation = cardElevation.value,
                            shape = RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 12.dp,
                                bottomStart = 4.dp,
                                bottomEnd = 12.dp
                            ),
                            spotColor = event.color.copy(alpha = 0.5f)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    event.color.copy(alpha = 0.95f),
                                    event.color.darker(0.1f).copy(alpha = 0.85f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(300f, 300f)
                            )
                        ),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .fillMaxWidth()
                    ) {
                        // Event Name with modern typography
                        Text(
                            text = event.title,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontFamily = karlaFont,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.25.sp,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 2f
                                )
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Time information with icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 0.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.time),
                                contentDescription = "Time",
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$startTimeFormat - $endTimeFormat",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                fontFamily = karlaFont,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Faculty information with icon
                        event.facultyName?.let { facultyName ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.whitehitman),
                                    contentDescription = "Hitman",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = facultyName,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 14.sp,
                                    fontFamily = karlaFont,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


suspend fun generateEvent(
    client: SupabaseClient,
    selectedDate: Date,
    roomNumber: String
): List<Event> {
    val columns = Columns.raw(
        """
        start_time,
        end_time,
        courses(
            course_name
        ),
        faculties(
            users(
                username
            ),
            color
        )
    """.trimIndent()
    )

    val rawEvents = client.from("timetable")
        .select(columns = columns) {
            filter {
                eq("class_date", selectedDate.toString())
                eq("classroom_id", roomNumber)
            }
        }.decodeList<RawEvent>()

    val events: List<Event> = rawEvents.map { it.toEvent() }

    return events
}
