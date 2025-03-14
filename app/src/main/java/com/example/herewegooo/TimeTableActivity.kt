package com.example.herewegooo


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.Event
import com.example.herewegooo.network.Period
import com.example.herewegooo.network.RawEvent
import com.example.herewegooo.network.RawTimeTable
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.realtime.Column
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import okhttp3.Dispatcher
import java.sql.Date
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun Timetable(
    userViewModel: UserViewModel,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    // Time configuration
    val startHour = 7
    val endHour = 19
    val pixelsPerHour = 180.dp
    val totalHours = endHour - startHour
    val client = supabaseClient()

    // State management
    val currentDate = LocalDate.now()
    var expanded by remember { mutableStateOf(false) }

    // Colors - Fun and modern palette
    val backgroundColor = Color(0xFF1E1E2E)
    val headerColor = Color(0xFF2A2A3C)
    val accentColor = Color(0xFF9676DB)
    val dropDownListColor = Color(0xFF3A3658)
    val buttonColor = Color(0xFF4A3ABA)
    val textColor = Color.White

    // Date options setup
    val todayKey = currentDate.format(DateTimeFormatter.ISO_DATE)
    val dateOptions = remember(todayKey) {
        (0 until 14).map { offset ->
            LocalDate.now()
                .plusDays(offset.toLong())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }

    var selectedDate by remember { mutableStateOf(dateOptions.firstOrNull() ?: "Select Date") }
    var classes by remember { mutableStateOf<List<Period>>(emptyList()) }

    // Load classes when date changes
    LaunchedEffect(selectedDate) {
        val localDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        val newFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate = localDate.format(newFormatter)
        // Replace this with your actual data fetching logic
        val sqlDate = Date.valueOf(formattedDate)

        withContext(Dispatchers.IO) {
            classes = getTimetable(client = client, selectedDate = sqlDate, user = userViewModel)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with day and date info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = headerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Day of week display
                val dayOfWeek = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    .format(DateTimeFormatter.ofPattern("EEEE"))

                Text(
                    text = dayOfWeek.uppercase(),
                    color = accentColor,
                    fontFamily = funnelFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Date selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                            .format(DateTimeFormatter.ofPattern("dd MMMM, yyyy")),
                        color = textColor,
                        fontFamily = funnelFont,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Date selector button
                    Box {
                        IconButton(
                            onClick = { expanded = true },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date",
                                tint = textColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        DropdownMenu(
                            modifier = Modifier
                                .background(dropDownListColor)
                                .width(280.dp),
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            val itemHeight = 48.dp
                            val maxHeight = 250.dp
                            val totalHeight = (dateOptions.size * itemHeight).coerceAtMost(maxHeight)

                            Box(
                                modifier = Modifier
                                    .height(totalHeight)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Column {
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
                                                    fontSize = 16.sp,
                                                    fontWeight = if (date == selectedDate) FontWeight.Bold else FontWeight.Normal
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

                // Quick navigation buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickDateButton(
                        text = "Today",
                        color = accentColor,
                        onClick = {
                            selectedDate = dateOptions.first()
                        }
                    )

                    QuickDateButton(
                        text = "Tomorrow",
                        color = accentColor.copy(alpha = 0.8f),
                        onClick = {
                            if (dateOptions.size > 1) {
                                selectedDate = dateOptions[1]
                            }
                        }
                    )
                }
            }
        }

        // Schedule view
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = headerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((totalHours * pixelsPerHour.value).dp)
                    .padding(vertical = 16.dp, horizontal = 8.dp)
            ) {
                // Time column
                TimelineComponent(
                    modifier = Modifier
                        .width(75.dp)
                        .fillMaxHeight()
                        .offset(x = (-10).dp),
                    startHour = startHour,
                    endHour = endHour,
                    pixelsPerHour = pixelsPerHour,
                    accentColor = accentColor
                )

                // Classes column
                ClassesComponent(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .offset(x = (-5).dp),
                    classes = classes,
                    startHour = startHour,
                    pixelsPerHour = pixelsPerHour
                )
            }
        }

        // Bottom stats card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .offset(y = (-30).dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = headerColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        title = "Total Classes",
                        value = "${classes.size}",
                        painter = painterResource(id = R.drawable.school), //School
                        color = accentColor
                    )

                    StatItem(
                        title = "Total Hours",
                        value = "${calculateTotalHours(classes)}",
                        painter = painterResource(id = R.drawable.time), //Timer
                        color = accentColor.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickDateButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatItem(
    title: String,
    value: String,
    painter: Painter,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TimelineComponent(
    modifier: Modifier = Modifier,
    startHour: Int,
    endHour: Int,
    pixelsPerHour: Dp,
    accentColor: Color
) {
    val lineColor = Color(0xFF4D4D56)
    val hourLineColor = Color(0xFF4D4D56)

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
                        // Time label with styling
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

                            // Full Hour line
                            HorizontalDivider(
                                modifier = Modifier
                                    .width(19.dp)
                                    .offset(x = 52.dp),
                                color = hourLineColor.copy(alpha = 0.7f)
                            )
                        }

                        // Horizontal divider
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

                    // Half Hour indicator
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = pixelsPerHour / 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Half-hour label
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

                            // Half Hour line
                            HorizontalDivider(
                                modifier = Modifier
                                    .width(30.dp)
                                    .offset(x = 41.dp),
                                color = lineColor.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClassesComponent(
    modifier: Modifier = Modifier,
    classes: List<Period>,
    startHour: Int,
    pixelsPerHour: Dp
) {
    Box(modifier = modifier) {
        // Timeline indicator
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

        classes.forEach { classEvent ->
            val startMinutes = classEvent.startTime.toSecondOfDay() / 60
            val minutesFromStart = startMinutes - (startHour * 60)
            val durationInMinutes = Duration.between(classEvent.startTime, classEvent.endTime).toMinutes()
            val durationHours = durationInMinutes / 60f
            val offsetY = (minutesFromStart / 60f * pixelsPerHour.value).dp

            val startTimeFormat = classEvent.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            val endTimeFormat = classEvent.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))

            val cardElevation = 4.dp

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .offset(x = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .height((durationHours * pixelsPerHour.value).dp - 2.dp)
                        .width(3.dp)
                        .clip(RoundedCornerShape(1.5.dp))
                        .background(classEvent.color.copy(alpha = 0.8f))
                        .shadow(1.dp, RoundedCornerShape(1.5.dp))
                )

                // Class card with modern styling
                val cornerSize = 16.dp
                Box(
                    modifier = Modifier
                        .offset(y = offsetY, x = 4.dp)
                        .height((durationHours * pixelsPerHour.value).dp - 2.dp)
                        .width(274.dp)
                        .clip(RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = cornerSize, bottomEnd = cornerSize))
                        .shadow(
                            elevation = cardElevation,
                            shape = RoundedCornerShape(cornerSize),
                            spotColor = classEvent.color.copy(alpha = 0.5f)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    classEvent.color.copy(alpha = 0.95f),
                                    classEvent.color.darker(0.1f).copy(alpha = 0.85f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(300f, 300f)
                            )
                        ),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        // Subject name
                        Text(
                            text = classEvent.subjectName,
                            color = Color.White,
                            fontSize = 20.sp,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time information
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 0.dp)
                                .offset(y = (-5).dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.time), //Access Time
                                contentDescription = "Time",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
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

                        // Room information
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 0.dp)

                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.room),
                                contentDescription = "Room",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Room ${classEvent.roomNumber}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp,
                                fontFamily = karlaFont,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // If the class is long enough, we add more details
                        if (durationInMinutes >= 45) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                                    .zIndex(2f),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                StatusChip(text = classEvent.status)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(text: String) {
    val chipColor = when(text.lowercase()) {
        "ongoing" -> Color(0xFF4CAF50)
        "upcoming" -> Color(0xFF2196F3)
        "completed" -> Color(0xFF9E9E9E)
        "canceled" -> Color(0xFFF44336)
        else -> Color(0xFF6C56F9)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, chipColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = chipColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


// Helper function to calculate total hours of classes
private fun calculateTotalHours(classes: List<Period>): Float {
    var totalMinutes = 0f
    classes.forEach { classEvent ->
        totalMinutes += Duration.between(classEvent.startTime, classEvent.endTime).toMinutes()
    }
    return (totalMinutes / 60).toFloat()
}


// Extension function to make colors darker
fun Color.darker(factor: Float): Color {
    return Color(
        red = this.red * (1 - factor),
        green = this.green * (1 - factor),
        blue = this.blue * (1 - factor),
        alpha = this.alpha
    )
}



suspend fun getTimetable(
    client: SupabaseClient,
    selectedDate: Date,
    user: UserViewModel
): List<Period>{
    val columns = Columns.raw(
        """
        start_time,
        end_time,
        courses(
            course_name
        ),
        classroom_id
    """.trimIndent()
    )

    val timeTable = client.from("timetable").select(columns = columns){
        filter {
            eq("class_date", selectedDate)
            eq("faculty_id", user.userId)
        }
    }.decodeList<RawTimeTable>()

    val colors = listOf(
        Color(0xFF6C56F9), // Purple
        Color(0xFF4CAF50), // Green
        Color(0xFF2196F3), // Blue
        Color(0xFFFF9800), // Orange
        Color(0xFFE91E63), // Pink
        Color(0xFF009688), // Teal
        Color(0xFF673AB7), // Deep Purple
        Color(0xFF3F51B5), // Indigo
        Color(0xFF795548), // Brown
        Color(0xFF607D8B), // Blue Grey
        Color(0xFFFF5722), // Deep Orange
        Color(0xFF8BC34A), // Light Green
        Color(0xFF00BCD4), // Cyan
        Color(0xFFFFEB3B), // Yellow
        Color(0xFF9C27B0), // Purple
        Color(0xFF20B2AA), // Light Sea Green
        Color(0xFF1E90FF), // Dodger Blue
        Color(0xFFFF1493), // Deep Pink
        Color(0xFF228B22), // Forest Green
        Color(0xFFDC143C), // Crimson
    )

    val todayDateTime = LocalDateTime.now()
//    val classDate = selectedDate.toLocalDate()

    val classDate = LocalDate.parse(selectedDate.toString())

    return timeTable.mapIndexed { index, rawTimeTable ->

        val classStartDateTime = LocalDateTime.of(classDate, rawTimeTable.startTime)
        val classEndDateTime = LocalDateTime.of(classDate, rawTimeTable.endTime)

        val status = when {
            todayDateTime.isBefore(classStartDateTime) -> "Upcoming"
            todayDateTime.isAfter(classEndDateTime) -> "Completed"
            else -> "Ongoing"
        }

        val color = colors[index % colors.size]

        Period(
            subjectName = rawTimeTable.title.courseName,
            startTime = rawTimeTable.startTime,
            endTime = rawTimeTable.endTime,
            roomNumber = rawTimeTable.classroom_id.toString(),
            color = color,
            status = status
        )
    }
}