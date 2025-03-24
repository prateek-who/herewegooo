package com.example.herewegooo


import android.util.Log
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
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
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.SubjectList
import com.example.herewegooo.network.SwapReceiveRequest
import com.example.herewegooo.network.TestTime
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
import java.util.Locale


@Composable
fun SwapAcceptDialogue(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    bookingDate: String,
    roomNumber: String,
    fromTime: LocalTime,
    toTime: LocalTime,
    toFacultyId: String,
    fromFacultyId: String,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val client = supabaseClient()

    // Course selection state
    var courseSearchText by remember { mutableStateOf("") }
    var courseExpanded by remember { mutableStateOf(false) }
    var selectedCourseId by remember { mutableStateOf("") }
    var selectedCourseName by remember { mutableStateOf("") }

    // Loading states
    var isCourseLoading by remember { mutableStateOf(true) }

    // Course data
    var courseList by remember { mutableStateOf<List<SubjectList>>(emptyList()) }

    // Colors matching BookingDialog
    val backgroundColor = Color(0xFF1E1E2E)    // Deep blue background
    val cardBackground = Color(0xFF272C3D)     // Slightly brighter card background
    val accentColor = Color(0xFF9676DB)        // Bright blue/purple accent
    val buttonColor = Color(0xFF16B1AC)        // Teal button color
    val textColor = Color(0xFFF8F8FF)          // Bright white text
    val highlightColor = Color(0xFF72F2EB)     // Teal highlight color
    val unfocusedFieldColor = Color(0xFF1E2239) // Darker field background
    val focusedFieldColor = Color(0xFF222845)   // Slightly lighter field background

    // Load course list
    LaunchedEffect(Unit) {
        try {
            val fetchedCourseList = getSubjectList(client)
            courseList = fetchedCourseList
            isCourseLoading = false
        } catch (e: Exception) {
            Log.e("SwapAcceptDialogue", "Error loading course list", e)
            isCourseLoading = false
        }
    }

    // Filter courses based on search text
    val filteredCourses by remember(courseSearchText, courseList) {
        derivedStateOf {
            if (courseSearchText.isEmpty()) {
                courseList
            } else {
                courseList.filter { course ->
                    course.course_name.contains(courseSearchText, ignoreCase = true) ||
                            course.course_id?.contains(courseSearchText, ignoreCase = true) == true
                }
            }
        }
    }

    if (openDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = backgroundColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 750.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = accentColor.copy(alpha = 0.4f)
                    ),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            highlightColor.copy(alpha = 0.4f),
                            accentColor.copy(alpha = 0.4f)
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBackground)
                            .padding(vertical = 20.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.swap),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = accentColor
                            )
                            Text(
                                text = "Swap Class",
                                color = textColor,
                                fontFamily = bungeeFont,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Form content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
//                        // Faculty Selection
//                        Column(
//                            modifier = Modifier.fillMaxWidth(),
//                            verticalArrangement = Arrangement.spacedBy(12.dp)
//                        ) {
//                            Text(
//                                text = "SELECT FACULTY",
//                                color = textColor.copy(alpha = 0.7f),
//                                fontFamily = karlaFont,
//                                fontSize = 12.sp,
//                                letterSpacing = 1.sp,
//                                fontWeight = FontWeight.Bold
//                            )
//
//                            Box(modifier = Modifier.fillMaxWidth()) {
//                                OutlinedTextField(
//                                    value = facultySearchText,
//                                    onValueChange = {
//                                        facultySearchText = it
//                                        facultyExpanded = true
//                                        selectedFacultyId = ""
//                                        selectedFacultyName = ""
//                                    },
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .shadow(4.dp, RoundedCornerShape(12.dp)),
//                                    shape = RoundedCornerShape(12.dp),
//                                    textStyle = TextStyle(
//                                        fontFamily = karlaFont,
//                                        fontSize = 16.sp,
//                                        color = textColor
//                                    ),
//                                    placeholder = {
//                                        Text(
//                                            text = "Search faculty by name",
//                                            fontFamily = karlaFont,
//                                            color = textColor.copy(alpha = 0.5f)
//                                        )
//                                    },
//                                    colors = OutlinedTextFieldDefaults.colors(
//                                        cursorColor = highlightColor,
//                                        focusedBorderColor = accentColor,
//                                        unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
//                                        focusedContainerColor = focusedFieldColor,
//                                        unfocusedContainerColor = unfocusedFieldColor
//                                    ),
//                                    leadingIcon = {
//                                        Icon(
//                                            painter = painterResource(id = R.drawable.person),
//                                            contentDescription = "Faculty",
//                                            tint = highlightColor,
//                                            modifier = Modifier.size(20.dp)
//                                        )
//                                    },
//                                    trailingIcon = {
//                                        IconButton(onClick = { facultyExpanded = !facultyExpanded }) {
//                                            Icon(
//                                                imageVector = if (facultyExpanded) Icons.Rounded.KeyboardArrowUp
//                                                else Icons.Rounded.KeyboardArrowDown,
//                                                contentDescription = if (facultyExpanded) "Collapse" else "Expand",
//                                                tint = accentColor
//                                            )
//                                        }
//                                    }
//                                )
//
//                                if (selectedFacultyId.isNotEmpty()) {
//                                    Text(
//                                        text = "Selected: $selectedFacultyName",
//                                        color = highlightColor,
//                                        fontFamily = karlaFont,
//                                        fontSize = 12.sp,
//                                        modifier = Modifier
//                                            .align(Alignment.BottomStart)
//                                            .padding(start = 16.dp, bottom = 6.dp)
//                                    )
//                                }
//                            }
//                        }

                        // Subject Selection
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "SELECT SUBJECT",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = karlaFont,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = courseSearchText,
                                    onValueChange = {
                                        courseSearchText = it
                                        courseExpanded = true
                                        selectedCourseId = ""
                                        selectedCourseName = ""
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = TextStyle(
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        color = textColor
                                    ),
                                    placeholder = {
                                        Text(
                                            text = "Search subject by name or code",
                                            fontFamily = karlaFont,
                                            color = textColor.copy(alpha = 0.5f)
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        cursorColor = highlightColor,
                                        focusedBorderColor = accentColor,
                                        unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                                        focusedContainerColor = focusedFieldColor,
                                        unfocusedContainerColor = unfocusedFieldColor
                                    ),
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.description),
                                            contentDescription = "Subject",
                                            tint = highlightColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
//                                    trailingIcon = {
//                                        IconButton(onClick = { courseExpanded = !courseExpanded }) {
//                                            Icon(
//                                                imageVector = if (courseExpanded) Icons.Rounded.KeyboardArrowUp
//                                                else Icons.Rounded.KeyboardArrowDown,
//                                                contentDescription = if (courseExpanded) "Collapse" else "Expand",
//                                                tint = accentColor
//                                            )
//                                        }
//                                    }
                                )
                                if (selectedCourseId.isNotEmpty()) {
                                    Text(
                                        text = "Selected: $selectedCourseId - $selectedCourseName",
                                        color = highlightColor,
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(start = 10.dp, top = 60.dp)
                                    )
                                }

                                val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

                                DropdownMenu(
                                    expanded = courseExpanded && filteredCourses.isNotEmpty() &&
                                            (!isKeyboardVisible || filteredCourses.size <= 3),
                                    onDismissRequest = { courseExpanded = false },
                                    modifier = Modifier
                                        .heightIn(max = 260.dp)
                                        .fillMaxWidth(0.8f)
                                        .background(cardBackground),
                                    properties = PopupProperties(focusable = false)
                                ) {
                                    if (isCourseLoading) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = accentColor)
                                        }
                                    } else {
                                        filteredCourses.forEach { course ->
                                            DropdownMenuItem(
                                                onClick = {
                                                    selectedCourseId = course.course_id ?: ""
                                                    selectedCourseName = course.course_name
                                                    courseSearchText = course.course_name
                                                    courseExpanded = false
                                                },
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = course.course_name,
                                                            color = textColor,
                                                            fontFamily = karlaFont,
                                                            fontSize = 16.sp,
                                                            modifier = Modifier.weight(1f)
                                                        )

                                                        if (course.course_id != null) {
                                                            Text(
                                                                text = course.course_id,
                                                                color = textColor.copy(alpha = 0.6f),
                                                                fontFamily = karlaFont,
                                                                fontSize = 14.sp
                                                            )
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = unfocusedFieldColor,
                                contentColor = textColor
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        accentColor.copy(alpha = 0.3f),
                                        buttonColor.copy(alpha = 0.3f)
                                    )
                                )
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = karlaFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = {
                                if (selectedCourseId.isEmpty()) {
                                    onShowSnackbar("Please select both faculty and subject", SnackbarType.ERROR)
                                    return@Button
                                }

                                coroutineScope.launch {
                                    try {
                                        swapInTimeTable(
                                            client = client,
                                            bookingDate = bookingDate,
                                            roomNumber = roomNumber,
                                            fromTime = fromTime,
                                            toTime = toTime,
                                            toFacultyId = toFacultyId,
                                            fromFacultyId = fromFacultyId,
                                            newCourseId = selectedCourseId
                                        )
                                        onDismiss()
                                        onShowSnackbar("Class successfully swapped", SnackbarType.SUCCESS)
                                    } catch (e: Exception) {
                                        Log.e("SwapAcceptDialogue", "Error swapping class", e)
                                        onShowSnackbar("Failed to swap class. Try again.", SnackbarType.ERROR)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1.5f)
                                .height(52.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = textColor
                            ),
                            contentPadding = PaddingValues(0.dp),
                            enabled = selectedCourseId.isNotEmpty()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(accentColor, accentColor)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.swap), //SwapHoriz
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Confirm Swap",
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
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


// Updated swapInTimeTable function
suspend fun swapInTimeTable(
    client: SupabaseClient,
    bookingDate: String,
    roomNumber: String,
    fromTime: LocalTime,
    toTime: LocalTime,
    toFacultyId: String,
    fromFacultyId: String,
    newCourseId: String
) {
    try {
        val timetableId = client.from("timetable").select(columns = Columns.list("id")){
            filter {
                eq("class_date", bookingDate)
                eq("start_time", fromTime)
                eq("end_time", toTime)
                eq("classroom_id", roomNumber)
                eq("faculty_id", fromFacultyId)
            }
        }.decodeSingle<TestTime>()

        client.from("timetable").update(
            {
                set("faculty_id", toFacultyId)
                set("course_id", newCourseId)
            }
        ) {
            filter {
                eq("id", timetableId.id)
            }
        }

        val swapTableId = client.from("swaprequest").select(columns = Columns.list("id")){
            filter {
                eq("from_id", fromFacultyId)
                eq("to_id", toFacultyId)
                eq("class_date", bookingDate)
                eq("start_time", fromTime)
                eq("end_time", toTime)
                eq("classroom_id", roomNumber)
                eq("status", "pending")
            }
        }.decodeSingle<TestTime>()

        client.from("swaprequest").update(
            {
                set("status", "approved")
            }
        ){
            filter {
                eq("id", swapTableId.id)
            }
        }
    } catch (e: Exception) {
        Log.e("SwapInTimeTable", "Error updating timetable", e)
        throw e
    }
}