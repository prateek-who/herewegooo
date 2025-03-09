package com.example.herewegooo

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
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
    val startHour = 7
    val endHour = 20
    val pixelsPerHour = 180.dp
    val totalHours = endHour - startHour

    val client = supabaseClient()

    val currentDate = LocalDate.now()

    var showDialog by remember { mutableStateOf(false) }

    val topRowFonts = funnelFont
    var expanded by remember { mutableStateOf(false) }

    val todayKey = currentDate.format(DateTimeFormatter.ISO_DATE)
    val dateOptions = remember(todayKey) {
        // Recomputes only when todayKey changes (daily)
        (0 until 10).map { offset ->
            LocalDate.now()
                .plusDays(offset.toLong())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }

    var selectedDate by remember { mutableStateOf(dateOptions.firstOrNull() ?: "Select Date") }

    var events by remember { mutableStateOf<List<Event>>(emptyList()) }

    LaunchedEffect(selectedDate) {
        val localDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))

        val newFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val newlocalDate = localDate.format(newFormatter)

        val sqlDate = Date.valueOf(newlocalDate)

        events = generateEvent(client, sqlDate, room)
    }


    val backgroundColorChoice = Color(0xFF121218)
//    val gradientBackground = Brush.verticalGradient(
//        colors = gradientColorChoice,
//        startY = 0f,
//        endY = Float.POSITIVE_INFINITY
//    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColorChoice)
            .verticalScroll(rememberScrollState())
    ) {
        if (userViewModel.userRole == "teacher" || userViewModel.userRole == "admin") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF1F2933)),
//                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
//            Button(onClick = {
//
//            },
//                modifier = Modifier.fillMaxSize()
//            ) {
//
//            }

                Column(
                    modifier = Modifier.offset(x = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Floor No",
                        color = Color.White,
                        fontFamily = topRowFonts,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4F6BFF))
                    ) {
                        Text(
                            text = floor,
                            color = Color.White,
                            fontFamily = topRowFonts,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                Column(
                    modifier = Modifier.offset(x = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Room No",
                        color = Color.White,
                        fontFamily = topRowFonts,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4F6BFF))
                    ) {
                        Text(
                            text = room,
                            color = Color.White,
                            fontFamily = topRowFonts,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
//                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = {
                            showDialog = true
                        },
                        modifier = Modifier
                            .width(110.dp)
                            .height(50.dp)
                            .offset(x = (-15).dp, y = 0.dp),
//                            .fillMaxSize(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2A4D6E)
                        ),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text(
                            text = "Book Slot",
                            fontFamily = topRowFonts,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFF1F2933)),
//                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.offset(x = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Floor No",
                        color = Color.White,
                        fontFamily = topRowFonts,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4F6BFF))
                    ) {
                        Text(
                            text = floor,
                            color = Color.White,
                            fontFamily = topRowFonts,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(0.1f))

                Column(
                    modifier = Modifier.offset(x = (-80).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Room No",
                        color = Color.White,
                        fontFamily = topRowFonts,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF4F6BFF))
                    ) {
                        Text(
                            text = room,
                            color = Color.White,
                            fontFamily = topRowFonts,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xCB1F2933)),
        ) {
            Text(
                text = "Date",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = 35.dp),
                color = Color.White,
                fontFamily = topRowFonts,
                fontSize = 26.sp,
                fontWeight = FontWeight.Normal
            )

//            Box (modifier = Modifier
//                .width(80.dp)
//                .height(36.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .background(Color(0xFFFFBA00))) {
            Box(
                modifier = Modifier
                    .width(152.dp)
                    .align(Alignment.Center)
                    .offset(x = (-10).dp)
                    .wrapContentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF4F6BFF))
            ) {

                Text(
                    text = selectedDate,
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.White,
                    fontFamily = topRowFonts,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.offset(x = 16.dp, y = 50.dp)) {
                    DropdownMenu(
                        modifier = Modifier
//                        .offset(x = 100.dp)
                            .background(Color(0xFF2A4D6E))
                            .align(Alignment.Center),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        val itemHeight = 48.dp // Approximate height of DropdownMenuItem
                        val maxHeight = 250.dp
                        val totalHeight = (dateOptions.size * itemHeight).coerceAtMost(maxHeight)

                        Box(
                            modifier = Modifier
                                .height(totalHeight)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column {
                                dateOptions.forEach { date ->
                                    DropdownMenuItem(
                                        onClick = {
                                            selectedDate = date
                                            expanded = false
                                        },
                                        text = {
                                            Text(
                                                text = date,
                                                color = Color.White,
                                                fontFamily = topRowFonts,
                                                fontSize = 15.sp,
                                                modifier = Modifier.offset(x = 10.dp)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = 78.dp)
                    .width(30.dp),
                onClick = {
                    expanded = true
                },
                contentPadding = PaddingValues(0.dp),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "DropdownArrow",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((totalHours * pixelsPerHour.value).dp)
        ) {
            TimelineComponent(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight(),
                startHour = startHour,
                endHour = endHour,
                pixelsPerHour = pixelsPerHour
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
        if (showDialog){
            MyDialog(
                openDialog = true,
                onDismiss = { showDialog = false },
                roomNumber = room,
                user = userViewModel,
                onShowSnackbar = onShowSnackbar
            )
        }
    }
}

@Composable
fun TimelineComponent(
    modifier: Modifier = Modifier,
    startHour: Int,
    endHour: Int,
    pixelsPerHour: Dp
) {

    val lineColor = Color.Gray

    Column(modifier = modifier) {
        (startHour until endHour).forEach { hour ->
            Box(
                modifier = Modifier
                    .height(pixelsPerHour)
                    .fillMaxWidth()
            ) {
                // Full Hour stuff
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocalTime.of(hour, 0)
                            .format(DateTimeFormatter.ofPattern("h:mm a")),
                        modifier = Modifier
                            .padding(start = 8.dp),
                        fontFamily = oswaldFont,
//                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFFA2A2A8)
//                    style = MaterialTheme.typography.bodySmall
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .width(300.dp)
                            .padding(start = 8.dp),
                        color = lineColor
                    )
                }

                // Half Hour stuff
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = LocalTime.of(hour, 30)
                            .format(DateTimeFormatter.ofPattern("h:mm a")),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .offset(y = pixelsPerHour / 2),
                        fontFamily = oswaldFont,
                        fontSize = 12.sp,
                        color = Color(0xFFA2A2A8)
                    )

                    HorizontalDivider(
                        modifier = Modifier
                            .width(40.dp)
                            .padding(start = 8.dp)
                            .offset(y = pixelsPerHour / 2),
                        color = lineColor
                    )
                }
            }
        }
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
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(3.dp)
                .offset(x = 2.dp)
                .background(Color.Gray.copy(alpha = 0.2f))
        )
        events.forEach { event ->
            val startMinutes = event.startTime.toSecondOfDay() / 60
            val minutesFromStart = startMinutes - (startHour * 60)
            val durationInMinutes = Duration.between(event.startTime, event.endTime).toMinutes()
            val durationHours = durationInMinutes / 60f
            val offsetY = (minutesFromStart / 60f * pixelsPerHour.value).dp

            val startTimeFormat = event.startTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            val endTimeFormat = event.endTime.format(DateTimeFormatter.ofPattern("h:mm a"))

            val tryThisFont = karlaFont

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .offset(x = 2.dp)
            ) {
                // Line that appears first
                Box(
                    modifier = Modifier
                        .offset(y = offsetY)
                        .height((durationHours * pixelsPerHour.value).dp - 2.dp)
                        .width(3.dp)
                        .background(event.color.copy(alpha = 1f))
                )

                // The box that holds the details of the event
                Box(
                    modifier = Modifier
                        .offset(y = offsetY, x = 2.dp)
                        .height((durationHours * pixelsPerHour.value).dp -2.dp)
                        .width(295.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 0.dp,
                                topEnd = 10.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 10.dp
                            ))
//                        .border(width = 2.dp, color = event.color.copy(alpha = 1f), shape = RoundedCornerShape(5.dp))
//                        .background(Color.Transparent),
//                        .background(event.color.copy(alpha = 0.2f)),
                        .background(event.color.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Column(
//                        horizontalAlignment = Alignment.Start
                        modifier = Modifier.offset(x = 10.dp, y = 6.dp)
                    ) {
                        // Event Name
                        Text(
                            text = event.title,
                            color = Color(0xFFF0F0F5),
                            fontSize = 30.sp,
                            fontFamily = tryThisFont,
                            fontWeight = FontWeight.Bold,
                        )

                        // Event time (start - end)
                        Text(
                            text = "Time: $startTimeFormat - $endTimeFormat",
                            color = Color(0xFFF0F0F5),
                            fontSize = 16.sp,
                            fontFamily = tryThisFont,
                            fontWeight = FontWeight.Bold,
                        )

                        // Faculty Name subbed instead of if statement
                        event.facultyName?.let { facultyName ->
                            Text(
                                text = "Faculty: $facultyName",
                                color = Color(0xFFF0F0F5),
                                fontSize = 16.sp,
                                fontFamily = tryThisFont,
                            )
                        }
                    }
                }
                // Add Box here for images and other shit. Lmaooo we ain't adding this feature
            }
        }
    }
}

//data class Event(
//    val date: LocalDate,
//    val startTime: LocalTime,
//    val endTime: LocalTime,
//    val title: String,
//    val facultyName: String? = null,
//    val color: Color = Color(0xFF4CAF50)
//)

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
    //    ---------------------
}
//    Event(
//        startTime = LocalTime.of(7, 50),
//        endTime = LocalTime.of(8, 40),
//        title = "1st Period",
//        facultyName = "Dr Sanjeev Kumar",
//        color = Color(0xFF2196F3)
//    ),
