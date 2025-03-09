package com.example.herewegooo


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
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
){
    val coroutineScope = rememberCoroutineScope()
    val client = supabaseClient()

    var cardTitle by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }

    val subjectEntries = staticSubjectMap.entries.toList()

    val filteredSubjects by remember(cardTitle) {
        derivedStateOf {
            if (cardTitle.isEmpty()) {
                subjectEntries
            } else {
                subjectEntries.filter { (key, value) ->
                    value.contains(cardTitle, ignoreCase = true)
                }
            }
        }
    }

    var selectedSubjectCode by remember { mutableStateOf("") }

    // Custom field info
    var isCustomSelected by remember { mutableStateOf(false) }
    var customSubjectName by remember { mutableStateOf("") }
    var customSubjectId by remember { mutableStateOf("") }


    if (openDialog){
        Dialog(onDismissRequest = onDismiss) {
            Surface (
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF131315),
                modifier = Modifier
                    .padding(16.dp)
                    .width(400.dp)
                    .height(340.dp)
            ) {
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column (
                        modifier = Modifier.fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFFFF3A3A)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Booking Conformation",
                            color = Color(0xFFEAEAEA),
                            fontFamily = oswaldFont,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        modifier = Modifier.height(50.dp),
                        value = cardTitle,
                        onValueChange = { newText ->
                            cardTitle = newText
                            expanded = true
                            isCustomSelected = false
                        },
                        shape = RoundedCornerShape(25.dp),
                        textStyle = TextStyle(
                            fontFamily = funnelFont,
                            fontSize = 20.sp,
                            color = Color(0xFFFAFAFA)
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = Color(0xFFFAFAFA),
                            focusedBorderColor = Color(0xFFA5A5A5)
                        ),
                        placeholder = {
                            Text(
                                text = "Enter Subject",
                                fontFamily = funnelFont
                            )
                        }
                    )
                    Box( modifier = Modifier.offset(x = (-125).dp, y = 2.dp)
                    ){
                        DropdownMenu(
                            expanded = expanded && filteredSubjects.isNotEmpty(),
                            onDismissRequest = { expanded = false },
                            properties = PopupProperties(focusable = false),
                            modifier = Modifier
                                .width(250.dp)
                                .background(Color(0xFF242432))
                                .border(width = 2.dp, color = Color(0xFF3C3C3E))
                        ) {
                            val itemHeight = 48.dp // Approximate height of DropdownMenuItem
                            val maxHeight = 250.dp
                            val totalHeight = (filteredSubjects.size * itemHeight).coerceAtMost(maxHeight)
                            Box(
                                modifier = Modifier
                                    .height(totalHeight)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Column {
                                    filteredSubjects.forEach { entry ->
                                        DropdownMenuItem(
                                            onClick = {
                                                cardTitle = entry.value
                                                expanded = false
                                                val key = entry.key
                                                if(key == null) {
                                                    isCustomSelected = true
                                                }else{
                                                    isCustomSelected = false
                                                    selectedSubjectCode = key
                                                }
                                            },
                                            text = {
                                                Text(
                                                    text = entry.value,
                                                    color = Color.White,
                                                    fontFamily = funnelFont,
                                                    fontSize = 18.sp,
                                                    modifier = Modifier.offset(x = 20.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if(isCustomSelected) {
                        OutlinedTextField(
                            value = customSubjectId,
                            onValueChange = { customSubjectId = it },
                            label = { Text(
                                text = "Custom Subject ID",
                                fontFamily = funnelFont
                            ) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            textStyle = TextStyle(
                                fontFamily = funnelFont,
                                fontSize = 18.sp,
                                color = Color(0xFFFAFAFA)
                            ),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                cursorColor = Color(0xFFFAFAFA),
                                focusedBorderColor = Color(0xFFA5A5A5),
                                unfocusedLabelColor = Color(0xFF5F5F5F),
                                focusedLabelColor = Color(0xFF5F5F5F)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = customSubjectName,
                            onValueChange = { customSubjectName = it },
                            label = {
                                Text(
                                    text = "Custom subject name",
                                    fontFamily = funnelFont
                                ) },
                            modifier = Modifier.fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            textStyle = TextStyle(
                                fontFamily = funnelFont,
                                fontSize = 18.sp,
                                color = Color(0xFFFAFAFA)
                            ),
                            shape = RoundedCornerShape(25.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                cursorColor = Color(0xFFFAFAFA),
                                focusedBorderColor = Color(0xFFA5A5A5),
                                unfocusedLabelColor = Color(0xFF5F5F5F),
                                focusedLabelColor = Color(0xFF5F5F5F)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Row {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .offset(x = (-10).dp)
                                .width(90.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB31212)
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = karlaFont,
                                fontSize = 16.sp,
//                                fontWeight = FontWeight.Bold,
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
                                            cardTitle
                                        ),
                                        customSubjectName = customSubjectName,
                                        customSubjectId = customSubjectId
                                    )
                                    onDismiss()
                                    onShowSnackbar("Message Approved", SnackbarType.SUCCESS)
                                }
                            },
                            modifier = Modifier
                                .offset(x = 0.dp)
                                .width(165.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B9543)
                            )
                        ) {
                            Text(
                                text = "Confirm Booking",
                                fontFamily = karlaFont,
                                fontSize = 17.sp,
//                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}


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