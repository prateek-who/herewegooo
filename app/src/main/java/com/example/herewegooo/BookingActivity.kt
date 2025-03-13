package com.example.herewegooo

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import com.example.herewegooo.data.model.UserViewModel
import com.example.herewegooo.network.IdColumnVerify
import com.example.herewegooo.network.Request
import com.example.herewegooo.network.sendRequest
import com.example.herewegooo.network.supabaseClient
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun BookingDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    roomNumber: String,
    user: UserViewModel,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    // Date selection
    val todayKey = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    val dateOptions = remember(todayKey) {
        (0 until 10).map { offset ->
            LocalDate.now()
                .plusDays(offset.toLong())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }
    var selectedDate by remember { mutableStateOf(dateOptions.firstOrNull() ?: "Select Date") }

    // Time selection state
    var fromHourEntry by remember { mutableStateOf("") }
    var fromMinuteEntry by remember { mutableStateOf("") }
    var fromMeridianEntry by remember { mutableStateOf("AM") }
    var toHourEntry by remember { mutableStateOf("") }
    var toMinuteEntry by remember { mutableStateOf("") }
    var toMeridianEntry by remember { mutableStateOf("AM") }
    var reason by remember { mutableStateOf("") }

    val client = supabaseClient()

    if (openDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1F1F23),
                modifier = Modifier
                    .padding(16.dp)
                    .width(400.dp)
                    .height(750.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Modern gradient header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4A6572),
                                        Color(0xFF344955)
                                    )
                                )
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.schedule),
                                contentDescription = "Book Slot",
//                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = "Book Your Slot",
                                modifier = Modifier,
                                fontSize = 26.sp,
                                fontFamily = oswaldFont,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Form content with scroll capability
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Room Number
                        ModernInfoField(
                            label = "Room Number",
                            value = roomNumber,
                            painter = painterResource(id = R.drawable.room)
                        )

                        // Date Selection
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A2A2E))
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.calendartoda),
                                        contentDescription = "Date",
                                        tint = Color(0xFF64B5F6),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Date",
                                            fontFamily = karlaFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF8F8F96)
                                        )
                                        Text(
                                            text = selectedDate,
                                            fontFamily = funnelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = Color.White
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { expanded = true },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = Color(0xFF3A3A3F),
                                            shape = CircleShape
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Select Date",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Date dropdown menu
                            DropdownMenu(
                                modifier = Modifier
                                    .width(300.dp)
                                    .background(Color(0xFF2A2A2E)),
                                expanded = expanded && dateOptions.isNotEmpty(),
                                onDismissRequest = { expanded = false }
                            ) {
                                val itemHeight = 48.dp
                                val maxHeight = 250.dp
                                val totalHeight =
                                    (dateOptions.size * itemHeight).coerceAtMost(maxHeight)

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
                                                        fontFamily = funnelFont,
                                                        fontSize = 18.sp
                                                    )
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // From Time Selection
                        ModernTimeSelection(
                            label = "From",
                            icon = painterResource(id = R.drawable.schedule),
                            hourValue = fromHourEntry,
                            onHourChange = { newValue ->
                                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                    fromHourEntry = newValue

                                    if (newValue.length == 2) {
                                        val intVal = newValue.toIntOrNull()
                                        if (intVal == null || intVal !in 1 until 13) {
                                            fromHourEntry = ""
                                        }
                                    }
                                }
                            },
                            minuteValue = fromMinuteEntry,
                            onMinuteChange = { newValue ->
                                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                    fromMinuteEntry = newValue

                                    if (newValue.length == 2) {
                                        val intVal = newValue.toIntOrNull()
                                        if (intVal == null || intVal !in 0 until 60) {
                                            fromMinuteEntry = ""
                                        }
                                    }
                                }
                            },
                            meridianValue = fromMeridianEntry,
                            onMeridianChange = { fromMeridianEntry = it }
                        )

                        // To Time Selection
                        ModernTimeSelection(
                            label = "To",
                            icon = painterResource(id = R.drawable.schedule),
                            iconTint = Color(0xFFFF7043),
                            hourValue = toHourEntry,
                            onHourChange = { newValue ->
                                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                    toHourEntry = newValue

                                    if (newValue.length == 2) {
                                        val intVal = newValue.toIntOrNull()
                                        if (intVal == null || intVal !in 1 until 13) {
                                            toHourEntry = ""
                                        }
                                    }
                                }
                            },
                            minuteValue = toMinuteEntry,
                            onMinuteChange = { newValue ->
                                if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                    toMinuteEntry = newValue

                                    if (newValue.length == 2) {
                                        val intVal = newValue.toIntOrNull()
                                        if (intVal == null || intVal !in 0 until 60) {
                                            toMinuteEntry = ""
                                        }
                                    }
                                }
                            },
                            meridianValue = toMeridianEntry,
                            onMeridianChange = { toMeridianEntry = it }
                        )

                        // Faculty Info
                        ModernInfoField(
                            label = "Faculty",
                            value = user.userName,
                            icon = Icons.Default.Person
                        )

                        // Reason for booking
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2A2A2E))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.description),
                                    contentDescription = "Reason",
                                    tint = Color(0xFF81C784),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Reason",
                                    fontFamily = karlaFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF8F8F96)
                                )
                            }

                            OutlinedTextField(
                                value = reason,
                                onValueChange = { reason = it },
                                placeholder = {
                                    Text(
                                        text = "Enter reason for booking slot",
                                        fontFamily = funnelFont,
                                        fontSize = 14.sp,
                                        color = Color(0xFF8F8F96)
                                    )
                                },
                                textStyle = TextStyle(
                                    fontFamily = funnelFont,
                                    fontSize = 16.sp,
                                    color = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(100.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF64B5F6),
                                    unfocusedBorderColor = Color(0xFF3A3A3F),
                                    cursorColor = Color(0xFF64B5F6),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button with updated style
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3A3A3F)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp,
                                pressedElevation = 2.dp
                            )
                        ) {
                            Text(
                                text = "Cancel",
                                fontFamily = oswaldFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Book slot button with updated style
                        Button(
                            onClick = {
                                val fromHour = fromHourEntry.toIntOrNull()
                                val fromMinute = fromMinuteEntry.toIntOrNull()
                                val toHour = toHourEntry.toIntOrNull()
                                val toMinute = toMinuteEntry.toIntOrNull()

                                if (fromHour == null || fromMinute == null || toHour == null || toMinute == null) {
                                    onShowSnackbar("Please enter valid times", SnackbarType.ERROR)
                                } else {
                                    fun convertToMinutes(
                                        hour: Int,
                                        minute: Int,
                                        meridian: String
                                    ): Int {
                                        var h = hour
                                        // Convert the hour to 24-hour format.
                                        if (meridian.uppercase() == "AM") {
                                            if (h == 12) h = 0
                                        } else if (meridian.uppercase() == "PM") {
                                            if (h != 12) h += 12
                                        }
                                        return h * 60 + minute
                                    }

                                    val fromTotal =
                                        convertToMinutes(fromHour, fromMinute, fromMeridianEntry)
                                    val toTotal =
                                        convertToMinutes(toHour, toMinute, toMeridianEntry)

                                    val now = LocalTime.now()
                                    val currentTotal = now.hour * 60 + now.minute

                                    val inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                                    val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                    val classDate = selectedDate
                                    val bookingDate = LocalDate.parse(classDate, inputFormatter)
                                    val finalDate =
                                        bookingDate.format(outputFormatter) // yyyy-MM-dd format

                                    val today = LocalDate.now()

                                    val startTime =
                                        getLocalTime(fromHour, fromMinute, fromMeridianEntry)
                                    val endTime = getLocalTime(toHour, toMinute, toMeridianEntry)

                                    // Validate booking time
                                    when {
                                        bookingDate.isBefore(today) -> {
                                            onShowSnackbar(
                                                "Cannot book for past dates",
                                                SnackbarType.ERROR
                                            )
                                        }

                                        bookingDate.isEqual(today) && fromTotal < currentTotal -> {
                                            onShowSnackbar(
                                                "Start time must be in the future",
                                                SnackbarType.ERROR
                                            )
                                        }

                                        toTotal <= fromTotal -> {
                                            onShowSnackbar(
                                                "End time must be after start time",
                                                SnackbarType.ERROR
                                            )
                                        }

                                        reason.isBlank() -> {
                                            onShowSnackbar(
                                                "Please enter a reason for booking",
                                                SnackbarType.ERROR
                                            )
                                        }

                                        else -> {
                                            // Proceed with booking
                                            coroutineScope.launch {
                                                val success = slotBookingRequest(
                                                    client,
                                                    sendRequest(
                                                        finalDate.toString(),
                                                        startTime,
                                                        endTime,
                                                        user.userName,
                                                        roomNumber.toInt(),
                                                        reason
                                                    )
                                                )
                                                onDismiss()
                                                if (success != null) {
                                                    onShowSnackbar(
                                                        "Slot requested successfully!",
                                                        SnackbarType.SUCCESS
                                                    )
                                                } else {
                                                    onShowSnackbar(
                                                        "Failed to book slot",
                                                        SnackbarType.ERROR
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E88E5)
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Book",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Book Slot",
                                    fontFamily = oswaldFont,
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

@Composable
fun ModernInfoField(
    label: String,
    value: String,
    iconTint: Color = Color(0xFF64B5F6),
    icon: ImageVector? = null,
    painter: Painter? = null
) {
    require(icon != null || painter != null) { "Either icon or painter must be provided" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2A2A2E))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                icon != null -> Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )

                painter != null -> Icon(
                    painter = painter,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = iconTint
                )
            }
            Column {
                Text(
                    text = label,
                    fontFamily = karlaFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF8F8F96)
                )
                Text(
                    text = value,
                    fontFamily = funnelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ModernTimeSelection(
    label: String,
    icon: Painter,
    iconTint: Color = Color(0xFF64B5F6),
    hourValue: String,
    onHourChange: (String) -> Unit,
    minuteValue: String,
    onMinuteChange: (String) -> Unit,
    meridianValue: String,
    onMeridianChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2A2A2E))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                fontFamily = karlaFont,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xFF8F8F96)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Hour input
            OutlinedTextField(
                value = hourValue,
                onValueChange = onHourChange,
                placeholder = {
                    Text(
                        text = "HH",
                        fontFamily = funnelFont,
                        fontSize = 16.sp,
                        color = Color(0xFF8F8F96)
                    )
                },
                textStyle = TextStyle(
                    fontFamily = funnelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = iconTint,
                    unfocusedBorderColor = Color(0xFF3A3A3F),
                    cursorColor = iconTint,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Text(
                text = ":",
                fontFamily = funnelFont,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Color.White
            )

            // Minute input
            OutlinedTextField(
                value = minuteValue,
                onValueChange = onMinuteChange,
                placeholder = {
                    Text(
                        text = "MM",
                        fontFamily = funnelFont,
                        fontSize = 16.sp,
                        color = Color(0xFF8F8F96)
                    )
                },
                textStyle = TextStyle(
                    fontFamily = funnelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = iconTint,
                    unfocusedBorderColor = Color(0xFF3A3A3F),
                    cursorColor = iconTint,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            // AM/PM selector
            val meridianOptions = listOf("AM", "PM")
            var meridianExpanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .border(1.dp, Color(0xFF3A3A3F), RoundedCornerShape(4.dp))
                    .clickable { meridianExpanded = true },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = meridianValue,
                    fontFamily = funnelFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                DropdownMenu(
                    expanded = meridianExpanded,
                    onDismissRequest = { meridianExpanded = false },
                    modifier = Modifier
                        .width(100.dp)
                        .background(Color(0xFF2A2A2E))
                ) {
                    meridianOptions.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                onMeridianChange(option)
                                meridianExpanded = false
                            },
                            text = {
                                Text(
                                    text = option,
                                    fontFamily = funnelFont,
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}


fun getLocalTime(hour: Int, minute: Int, meridian: String): LocalTime {
    // Adjust the hour to 24-hour format.
    val adjustedHour = when (meridian.uppercase()) {
        "AM" -> if (hour == 12) 0 else hour
        "PM" -> if (hour < 12) hour + 12 else hour
        else -> hour
    }
    return LocalTime.of(adjustedHour, minute)
}


suspend fun slotBookingRequest(
    client: SupabaseClient,
    requestList: sendRequest
): Int? {
    val insertionDataList = Request(
        class_date = requestList.classDate,
        start_time = requestList.startTime,
        end_time = requestList.endTime,
        faculty_name = requestList.facultyName,
        classroom_id = requestList.classroomId,
        reason = requestList.reason
    )

    return try {
        val response = client.from("requests").insert(insertionDataList) {
            select(columns = Columns.list("id"))
        }.decodeSingle<IdColumnVerify>()
        response.id
    } catch (e: Exception) {
        println("Error during slot booking request ${e.localizedMessage}")
        null
    }
}