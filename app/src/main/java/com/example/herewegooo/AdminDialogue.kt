package com.example.herewegooo


import android.media.Image
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.CourseTableQuery
import com.example.herewegooo.network.DeletionId
import com.example.herewegooo.network.IdColumnVerify
import com.example.herewegooo.network.Request
import com.example.herewegooo.network.TeacherId
import com.example.herewegooo.network.courseInsertion
import com.example.herewegooo.network.finalEventConformation
import com.example.herewegooo.network.finalEventPushDataClass
import com.example.herewegooo.network.sendRequest
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


val staticSubjectMap = mapOf(
    "6C01" to "Computer Forensics",
    "6C02" to "Windows Azure",
    "6C03" to "Mobile Application Development",
    null to "Custom"
)

@Composable
fun AdminDialogue(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    bookingDate: String,
    roomNumber: String,
    fromTime: LocalTime,
    toTime: LocalTime,
    facultyName: String,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val client = supabaseClient()

    var subject by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedSubjectCode by remember { mutableStateOf("") }

    // Custom field info
    var isCustomSelected by remember { mutableStateOf(false) }
    var customSubjectName by remember { mutableStateOf("") }
    var customSubjectId by remember { mutableStateOf("") }

    val subjectEntries = staticSubjectMap.entries.toList()
    val filteredSubjects by remember(subject) {
        derivedStateOf {
            if (subject.isEmpty()) {
                subjectEntries
            } else {
                subjectEntries.filter { (_, value) ->
                    value.contains(subject, ignoreCase = true)
                }
            }
        }
    }

    // Format the date for display
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val parsedDate = LocalDate.parse(bookingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val formattedDate = parsedDate.format(dateFormatter)

    // Format times for display
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    val formattedStartTime = fromTime.format(timeFormatter)
    val formattedEndTime = toTime.format(timeFormatter)

    if (openDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1E22),
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .widthIn(max = 400.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF1B9543),  // Darker green
                                        Color(0xFF5DD261)
                                    )
                                )
                            )
                            .padding(vertical = 16.dp, horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.eventavailabl),
                                contentDescription = null,
//                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Confirm Booking",
                                color = Color.White,
                                fontFamily = oswaldFont,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Booking summary
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF242428))
                            .padding(16.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            BookingSummaryItem(
                                image = painterResource(id = R.drawable.calendartoda),
                                label = "Date",
                                value = formattedDate
                            )

                            BookingSummaryItem(
                                image = painterResource(id = R.drawable.accesstime),
                                label = "Time",
                                value = "$formattedStartTime - $formattedEndTime"
                            )

                            BookingSummaryItem(
                                image = painterResource(id = R.drawable.room),
                                label = "Room",
                                value = "Classroom $roomNumber"
                            )

                            BookingSummaryItem(
                                image = painterResource(id = R.drawable.person),
                                label = "Faculty",
                                value = facultyName
                            )
                        }
                    }

                    // Subject selection area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "ASSIGN SUBJECT",
                            color = Color(0xFFABABAF),
                            fontFamily = oswaldFont,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Dropdown field with autocomplete
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { newText ->
                                    subject = newText
                                    expanded = true
                                    isCustomSelected = false
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                textStyle = TextStyle(
                                    fontFamily = karlaFont,
                                    fontSize = 16.sp,
                                    color = Color(0xFFFAFAFA)
                                ),
                                placeholder = {
                                    Text(
                                        text = "Search or select a subject",
                                        fontFamily = karlaFont,
                                        color = Color(0xFF8E8E93)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    cursorColor = Color(0xFFFAFAFA),
                                    focusedBorderColor = Color(0xFF5E5CE6),
                                    unfocusedBorderColor = Color(0xFF3C3C3E),
                                    focusedContainerColor = Color(0xFF28282C),
                                    unfocusedContainerColor = Color(0xFF28282C)
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { expanded = !expanded }) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                                            contentDescription = if (expanded) "Collapse" else "Expand",
                                            tint = Color(0xFFABABAF)
                                        )
                                    }
                                }
                            )

                            DropdownMenu(
                                expanded = expanded && filteredSubjects.isNotEmpty(),
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 250.dp)
                                    .background(Color(0xFF28282C)),
                                properties = PopupProperties(focusable = false)
                            ) {
                                filteredSubjects.forEach { entry ->
                                        DropdownMenuItem(
                                            onClick = {
                                                subject = entry.value
                                                expanded = false
                                                val key = entry.key
                                                if (key == null) {
                                                    isCustomSelected = true
                                                } else {
                                                    isCustomSelected = false
                                                    selectedSubjectCode = key
                                                }
                                            },
                                            text = {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = entry.value,
                                                        color = Color.White,
                                                        fontFamily = karlaFont,
                                                        fontSize = 16.sp,
                                                    )

                                                    if (entry.key != null) {
                                                        Text(
                                                            text = entry.key.toString(),
                                                            color = Color(0xFF8E8E93),
                                                            fontFamily = karlaFont,
                                                            fontSize = 14.sp,
                                                        )
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }

                        // Custom subject fields
                        AnimatedVisibility(
                            visible = isCustomSelected,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = customSubjectId,
                                    onValueChange = { customSubjectId = it },
                                    label = {
                                        Text(
                                            text = "Custom Subject ID",
                                            fontFamily = karlaFont
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        color = Color(0xFFFAFAFA)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        cursorColor = Color(0xFFFAFAFA),
                                        focusedBorderColor = Color(0xFF5E5CE6),
                                        unfocusedBorderColor = Color(0xFF3C3C3E),
                                        focusedContainerColor = Color(0xFF28282C),
                                        unfocusedContainerColor = Color(0xFF28282C),
                                        unfocusedLabelColor = Color(0xFF8E8E93),
                                        focusedLabelColor = Color(0xFFABABAF)
                                    )
                                )

                                OutlinedTextField(
                                    value = customSubjectName,
                                    onValueChange = { customSubjectName = it },
                                    label = {
                                        Text(
                                            text = "Custom Subject Name",
                                            fontFamily = karlaFont
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    textStyle = TextStyle(
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        color = Color(0xFFFAFAFA)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        cursorColor = Color(0xFFFAFAFA),
                                        focusedBorderColor = Color(0xFF5E5CE6),
                                        unfocusedBorderColor = Color(0xFF3C3C3E),
                                        focusedContainerColor = Color(0xFF28282C),
                                        unfocusedContainerColor = Color(0xFF28282C),
                                        unfocusedLabelColor = Color(0xFF8E8E93),
                                        focusedLabelColor = Color(0xFFABABAF)
                                    )
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF3C3C3E)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
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
                                coroutineScope.launch {
                                    finalEventPush(
                                        client = client,
                                        finalEventList = finalEventConformation(
                                            bookingDate,
                                            fromTime,
                                            toTime,
                                            selectedSubjectCode,
                                            roomNumber.toInt(),
                                            facultyName,
                                            subject
                                        ),
                                        customSubjectName = customSubjectName,
                                        customSubjectId = customSubjectId
                                    )
                                    onDismiss()
                                    onShowSnackbar("Booking successfully approved", SnackbarType.SUCCESS)
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF51B654)
                            ),
                            enabled = subject.isNotEmpty() && (!isCustomSelected ||
                                    (isCustomSelected && customSubjectId.isNotEmpty() && customSubjectName.isNotEmpty()))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Confirm Booking",
                                    fontFamily = karlaFont,
                                    fontSize = 16.sp,
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

@Composable
private fun BookingSummaryItem(
    image: Painter,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
       Image(
            painter = image,
            contentDescription = null,
//            tint = Color(0xFF8E8E93),
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFF8E8E93),
                fontFamily = karlaFont,
                fontSize = 14.sp
            )

            Text(
                text = value,
                color = Color.White,
                fontFamily = karlaFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// AdminDenyDialogue would follow similar modernization patterns


suspend fun finalEventPush(
    client: SupabaseClient,
    finalEventList: finalEventConformation,
    customSubjectName: String,
    customSubjectId: String
){
    try{
        val teacherId = client.from("users").select (columns = Columns.list("user_id")) {
            filter {
            eq("username", finalEventList.faculty_name)
            }
        }.decodeSingle<TeacherId>()

        val requestDeleteId = client.from("requests").select(columns = Columns.list("id")) {
            filter {
                eq("class_date", finalEventList.classDate)
                eq("start_time", finalEventList.start_time)
                eq("end_time", finalEventList.end_time)
                eq("faculty_name", finalEventList.faculty_name)
                eq("classroom_id", finalEventList.classroom_id)
            }
        }.decodeSingle<DeletionId>()

        if (finalEventList.course_id == ""){
            val courseTableInsertion = courseInsertion(customSubjectId, customSubjectName, teacherId.user_id)
            val result = client.from("courses").insert(courseTableInsertion) {
                select()
            }.decodeSingle<CourseTableQuery>()
//            delay(1000)

            val insertInTimeTable = finalEventPushDataClass(
                finalEventList.classDate,
                finalEventList.start_time,
                finalEventList.end_time,
                result.course_id,
                finalEventList.classroom_id,
                teacherId.user_id)
            client.from("timetable").insert(insertInTimeTable)
        }else {
            val insertInTimeTable = finalEventPushDataClass(
                finalEventList.classDate,
                finalEventList.start_time,
                finalEventList.end_time,
                finalEventList.course_id.toString(),
                finalEventList.classroom_id,
                teacherId.user_id)
            client.from("timetable").insert(insertInTimeTable) {
            }
        }

        client.from("requests").delete{
            filter {
                eq("id", requestDeleteId.id)
            }
        }

    }catch (e: Exception){
        println("Error here: ${e.localizedMessage}")
    }
}