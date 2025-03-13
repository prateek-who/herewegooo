package com.example.herewegooo


import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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


@Composable
fun AdminDenyDialogue(
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

    if (openDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1E1E20),
                tonalElevation = 4.dp,
                modifier = Modifier
                    .width(400.dp)
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFD81F26),
                                        Color(0xFFFF5757)
                                    )
                                ),
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                            )
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Confirm Denial",
                            color = Color.White,
                            fontFamily = oswaldFont,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Are you sure you want to deny this request?",
                            color = Color(0xFFEAEAEA),
                            fontFamily = karlaFont,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Optional: Show request details
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .background(Color(0xFF2A2A2E), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                RequestInfoRow("Faculty", facultyName)
                                RequestInfoRow("Room", roomNumber)
                                RequestInfoRow("Date", formatDate(bookingDate))
                                RequestInfoRow("Time", "${formatTime(fromTime)} - ${formatTime(toTime)}")
                            }
                        }
                    }

                    // Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(100.dp)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFF676D6D)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFE1E6F1),
                            ),
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
                                    finalDenyConformation(
                                        client = client,
                                        finalEventDeny = sendRequest(
                                            bookingDate,
                                            fromTime,
                                            toTime,
                                            facultyName,
                                            roomNumber.toInt(),
                                            "who cares"
                                        )
                                    )
                                    onShowSnackbar("Request Denied", SnackbarType.SUCCESS)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE53935)
                            )
                        ) {
                            Text(
                                text = "Deny Request",
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

@Composable
private fun RequestInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF8F8F96),
            fontFamily = karlaFont,
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontFamily = karlaFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

// Helper function to format LocalTime
private fun formatTime(time: LocalTime): String {
    return time.format(DateTimeFormatter.ofPattern("h:mm a"))
}

private fun formatDate(dateStr: String): String {
    return try {
        val originalFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val targetFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        val date = LocalDate.parse(dateStr, originalFormat)
        date.format(targetFormat)
    } catch (e: Exception) {
        // Return original string if parsing fails
        dateStr
    }
}

suspend fun finalDenyConformation(
    client: SupabaseClient,
    finalEventDeny: sendRequest
){
    try {
        val requestDeleteId = client.from("requests").select(columns = Columns.list("id")) {
            filter {
                eq("class_date", finalEventDeny.classDate)
                eq("start_time", finalEventDeny.startTime)
                eq("end_time", finalEventDeny.endTime)
                eq("faculty_name", finalEventDeny.facultyName)
                eq("classroom_id", finalEventDeny.classroomId)
            }
        }.decodeSingle<DeletionId>()

        client.from("requests").delete{
            filter {
                eq("id", requestDeleteId.id)
            }
        }
    }catch (e: Exception){
        println("Ze problame iz: ${e.localizedMessage}")
    }
}
