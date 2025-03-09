package com.example.herewegooo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.layout.ModifierInfo
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
import com.example.herewegooo.network.supabaseClient
import com.example.herewegooo.network.toEvent
import com.example.herewegooo.ui.theme.HerewegoooTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.coroutineScope
import java.time.LocalDateTime
import java.sql.Date
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AdminPanel(
    modifier: Modifier = Modifier,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val client = supabaseClient()

    val requestsState = produceState<List<ReceiveRequests>?>(initialValue = null, client) {
        value = getRequests(client)
    }

    val requests = requestsState.value ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131315))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFFFF3A3A))
        ) {
            Text(
                text = "ADMIN PANEL",
                color = Color(0xFFEAEAEA),
                fontFamily = oswaldFont,
                fontSize = 80.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
        ) {
            items(requests) { request ->
                RequestItem(
                    request,
                    onShowSnackbar)
            }
        }
    }
}

@Composable
fun RequestItem(
    request: ReceiveRequests,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val smallColor = Color(0xFF77767b)
    val bigColor = Color(0xFFF0F0F5)

    var showDialog by remember { mutableStateOf(false) }
    var showDenyDialoge by remember { mutableStateOf(false) }

    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val parsedDate = LocalDate.parse(request.class_date, inputFormatter)
    val formattedDate = parsedDate.format(outputFormatter)

    val createdInputFormatter = DateTimeFormatter.ISO_DATE_TIME
    val createdOutputFormatterDate = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val createdOutputFormatterTime = DateTimeFormatter.ofPattern("HH:mma")

    val dateTime = LocalDateTime.parse(request.request_created, createdInputFormatter)
    val formattedDateRequest = dateTime.format(createdOutputFormatterDate)
    val formattedTimeRequest = dateTime.format(createdOutputFormatterTime)

    val spaceHeight = 7.dp

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Column {
                    Text(
                        text = "Date",
                        fontFamily = oswaldFont,
                        fontSize = 14.sp,
                        color = smallColor
                    )
                    Text(
                        text = formattedDate,
                        fontFamily = karlaFont,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = bigColor
                    )
                }
                Column (modifier = Modifier.offset(x = 100.dp)){
                    Text(
                        text = "Classroom",
                        fontFamily = oswaldFont,
                        fontSize = 14.sp,
                        color = smallColor
                    )
                    Text(
                        text = request.classroom_id.toString(),
                        fontFamily = karlaFont,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = bigColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(spaceHeight))

            Text(
                text = "Time",
                fontFamily = oswaldFont,
                fontSize = 14.sp,
                color = smallColor
            )
            Text(
                text = "${request.start_time.format(timeFormatter)} - ${request.end_time.format(timeFormatter)}",
                fontFamily = karlaFont,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = bigColor
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            Text(
                text = "Faculty",
                fontFamily = oswaldFont,
                fontSize = 14.sp,
                color = smallColor
            )
            Text(
                text = request.faculty_name,
                fontFamily = karlaFont,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = bigColor
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            Text(
                text = "Reason",
                fontFamily = oswaldFont,
                fontSize = 14.sp,
                color = smallColor
            )
            Text(
                text = request.reason,
                fontFamily = karlaFont,
                fontSize = 26.sp,
                color = bigColor
            )

            Spacer(modifier = Modifier.height(spaceHeight))

            Text(
                text = "Requested on",
                fontFamily = oswaldFont,
                fontSize = 14.sp,
                color = smallColor
            )
            Text(
                text = "$formattedDateRequest  at  $formattedTimeRequest",
                fontFamily = karlaFont,
                fontSize = 26.sp,
                color = bigColor
            )

            Row{
                Button(
                    onClick = {
                        showDenyDialoge = true
                    },
                    modifier = Modifier
                        .offset(x = 0.dp, y = 10.dp)
                        .width(165.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB31212)
                    )
                ) {
                    Text(
                        text = "Deny",
                        fontFamily = karlaFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = bigColor
                    )
                }
                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .offset(x = 10.dp, y = 10.dp)
                        .width(170.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B9543)
                    )
                ) {
                    Text(
                        text = "Approve",
                        fontFamily = karlaFont,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = bigColor
                    )
                }
            }
            if (showDialog){
                AdminDialogue(
                    openDialog = true,
                    onDismiss = { showDialog = false },
                    bookingDate = request.class_date,
                    roomNumber = request.classroom_id.toString(),
                    fromTime = request.start_time,
                    toTime = request.end_time,
                    facultyName = request.faculty_name,
                    onShowSnackbar = onShowSnackbar
                )
            }
            if (showDenyDialoge){
                AdminDenyDialogue(
                    openDialog = true,
                    onDismiss = { showDenyDialoge = false },
                    bookingDate = request.class_date,
                    roomNumber = request.classroom_id.toString(),
                    fromTime = request.start_time,
                    toTime = request.end_time,
                    facultyName = request.faculty_name,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
    }
}

suspend fun getRequests(client: SupabaseClient): List<ReceiveRequests> {
    val rawList =
        client.from("requests").select(columns = Columns.ALL).decodeList<ReceiveRequests>()

    return rawList
}