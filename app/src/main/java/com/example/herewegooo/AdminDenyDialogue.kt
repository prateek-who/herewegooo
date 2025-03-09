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
import androidx.compose.ui.text.style.TextAlign
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
){
    val coroutineScope = rememberCoroutineScope()
    val client = supabaseClient()

    if (openDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF131315),
                modifier = Modifier
                    .padding(16.dp)
                    .width(400.dp)
                    .height(160.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .height(85.dp)
                            .background(Color(0xFFFF3A3A))
                    ){
                        Text(
                            text = "Confirm denial of this request?",
                            color = Color(0xFFEAEAEA),
                            fontFamily = oswaldFont,
                            fontSize = 35.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .offset(x = (-5).dp, y = 8.dp),
                            style = TextStyle(
                                lineHeight = 32.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Row (
                        modifier = Modifier.offset(y = 10.dp)
                    ) {
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
                                .offset(x = 0.dp)
                                .width(165.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B9543)
                            )
                        ) {
                            Text(
                                text = "Deny",
                                fontFamily = karlaFont,
                                fontSize = 17.sp,
                            )
                        }
                    }

                }
            }
        }
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
