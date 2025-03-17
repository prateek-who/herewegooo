package com.example.herewegooo

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
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
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
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
import java.util.Locale


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

    // Format the selected date for display
    val displayDateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
    val parsedDate = try {
        LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    } catch (e: Exception) {
        LocalDate.now()
    }
    val formattedDisplayDate = parsedDate.format(displayDateFormatter)

    // Colors matching login screen
    val backgroundColor = Color(0xFF1E1E2E)    // Deep blue background
    val cardBackground = Color(0xFF272C3D)     // Slightly brighter card background
    val accentColor = Color(0xFF9676DB)        // Bright blue/purple accent
    val buttonColor = Color(0xFF16B1AC)        // Teal button color
    val textColor = Color(0xFFF8F8FF)          // Bright white text
    val highlightColor = Color(0xFF72F2EB)     // Teal highlight color
    val unfocusedFieldColor = Color(0xFF1E2239) // Darker field background
    val focusedFieldColor = Color(0xFF222845)   // Slightly lighter field background

    val client = supabaseClient()

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
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Gradient header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBackground)  // Use cardBackground instead of gradient
                            .padding(vertical = 20.dp, horizontal = 24.dp)
                            .border(
                                width = 0.dp,  // Remove border or make it subtle
                                color = Color.Transparent,
                                shape = RectangleShape
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.schedule),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = accentColor  // Use accent color instead of white
                            )
                            Text(
                                text = "Book Your Slot",
                                color = textColor,
                                fontFamily = bungeeFont,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                // Simplified text styling - removed shadow effect
                            )
                        }
                    }

                    // Form content with scroll capability
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp)
                    ) {
                        // Room info card with enhanced styling
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackground
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        highlightColor.copy(alpha = 0.3f),
                                        accentColor.copy(alpha = 0.3f)
                                    )
                                )
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.room),
                                    contentDescription = "Room Icon",
                                    modifier = Modifier.size(22.dp),
                                    tint = highlightColor
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "ROOM",
                                        fontFamily = karlaFont,
                                        fontSize = 12.sp,
                                        color = textColor.copy(alpha = 0.6f),
                                        letterSpacing = 0.5.sp
                                    )

                                    Text(
                                        text = "Classroom $roomNumber",
                                        fontFamily = karlaFont,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                }
                            }
                        }

                        // Date selection
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "SELECT DATE",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = karlaFont,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = formattedDisplayDate,
                                    onValueChange = { },
                                    enabled = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp),
                                    textStyle = TextStyle(
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        color = textColor
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        disabledBorderColor = accentColor.copy(alpha = 0.4f),
                                        disabledContainerColor = unfocusedFieldColor,
                                        disabledTextColor = textColor
                                    ),
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(
                                                imageVector = Icons.Rounded.ArrowDropDown,
                                                contentDescription = "Select Date",
                                                tint = accentColor
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.calendartoda),
                                            contentDescription = "Calendar",
                                            tint = highlightColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                )

                                // Make the whole field clickable
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { expanded = true }
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .background(cardBackground),
                                    properties = PopupProperties(focusable = false)
                                ) {
                                    dateOptions.forEach { date ->
                                        val formattedOptionDate = try {
                                            val parsed = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                            parsed.format(displayDateFormatter)
                                        } catch (e: Exception) {
                                            date
                                        }

                                        DropdownMenuItem(
                                            onClick = {
                                                selectedDate = date
                                                expanded = false
                                            },
                                            text = {
                                                Text(
                                                    text = formattedOptionDate,
                                                    color = textColor,
                                                    fontFamily = karlaFont,
                                                    fontSize = 16.sp
                                                )
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }

                        // Time selection section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ){
                            Text(
                                text = "SELECT TIME",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = karlaFont,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // From Time row
                            TimeSelectionRow(
                                label = "From",
                                hourValue = fromHourEntry,
                                onHourChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        fromHourEntry = newValue
                                        if (newValue.length == 2) {
                                            val intVal = newValue.toIntOrNull()
                                            if (intVal == null || intVal !in 1..12) {
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
                                            if (intVal == null || intVal !in 0..59) {
                                                fromMinuteEntry = ""
                                            }
                                        }
                                    }
                                },
                                meridianValue = fromMeridianEntry,
                                onMeridianChange = { fromMeridianEntry = it },
                                iconTint = buttonColor,
                                unfocusedFieldColor = unfocusedFieldColor,
                                focusedFieldColor = focusedFieldColor,
                                accentColor = accentColor,
                                textColor = textColor
                            )

                            // To Time row
                            TimeSelectionRow(
                                label = "To",
                                hourValue = toHourEntry,
                                onHourChange = { newValue ->
                                    if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                        toHourEntry = newValue
                                        if (newValue.length == 2) {
                                            val intVal = newValue.toIntOrNull()
                                            if (intVal == null || intVal !in 1..12) {
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
                                            if (intVal == null || intVal !in 0..59) {
                                                toMinuteEntry = ""
                                            }
                                        }
                                    }
                                },
                                meridianValue = toMeridianEntry,
                                onMeridianChange = { toMeridianEntry = it },
                                iconTint = accentColor,
                                unfocusedFieldColor = unfocusedFieldColor,
                                focusedFieldColor = focusedFieldColor,
                                accentColor = accentColor,
                                textColor = textColor
                            )
                        }

                        // Faculty info section
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = cardBackground
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
                            BookingSummaryItemTeacher(
                                image = painterResource(id = R.drawable.person),
                                label = "Faculty",
                                value = user.userName,
                                textColor = textColor,
                                accentColor = highlightColor
                            )
                        }

                        // Reason field
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "REASON FOR BOOKING",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = karlaFont,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = reason,
                                onValueChange = { reason = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 120.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(
                                    fontFamily = karlaFont,
                                    fontSize = 16.sp,
                                    color = textColor
                                ),
                                placeholder = {
                                    Text(
                                        text = "Enter reason for booking",
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
                                        contentDescription = "Reason",
                                        tint = highlightColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ){
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
                                                        user.userId,
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
                                .weight(1.5f)
                                .height(52.dp)
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = textColor
                            ),
                            contentPadding = PaddingValues(0.dp)
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
                                ){
                                    Icon(
                                        painter = painterResource(id = R.drawable.schedule),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Book Slot",
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

@Composable
fun TimeSelectionRow(
    label: String,
    hourValue: String,
    onHourChange: (String) -> Unit,
    minuteValue: String,
    onMinuteChange: (String) -> Unit,
    meridianValue: String,
    onMeridianChange: (String) -> Unit,
    iconTint: Color,
    unfocusedFieldColor: Color,
    focusedFieldColor: Color,
    accentColor: Color,
    textColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = unfocusedFieldColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = accentColor.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Label with icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .width(80.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.schedule),
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label,
                    color = textColor,
                    fontFamily = karlaFont,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Time inputs
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Hour input
                OutlinedTextField(
                    value = hourValue,
                    onValueChange = onHourChange,
                    placeholder = {
                        Text(
                            text = "HH",
                            fontFamily = karlaFont,
                            fontSize = 14.sp,
                            color = textColor.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = karlaFont,
                        fontSize = 16.sp,
                        color = textColor,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = iconTint,
                        unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                        cursorColor = textColor,
                        focusedContainerColor = focusedFieldColor,
                        unfocusedContainerColor = unfocusedFieldColor
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Text(
                    text = ":",
                    color = textColor,
                    fontFamily = karlaFont,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Minute input
                OutlinedTextField(
                    value = minuteValue,
                    onValueChange = onMinuteChange,
                    placeholder = {
                        Text(
                            text = "MM",
                            fontFamily = karlaFont,
                            fontSize = 14.sp,
                            color = textColor.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    textStyle = TextStyle(
                        fontFamily = karlaFont,
                        fontSize = 16.sp,
                        color = textColor,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = iconTint,
                        unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                        cursorColor = textColor,
                        focusedContainerColor = focusedFieldColor,
                        unfocusedContainerColor = unfocusedFieldColor
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                // AM/PM selector
                var meridianExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
                        color = unfocusedFieldColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable { meridianExpanded = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            // AM/PM text
                            Text(
                                text = meridianValue,
                                fontFamily = karlaFont,
                                fontSize = 16.sp,
                                color = textColor,
                                textAlign = TextAlign.Center
                            )

                            // Dropdown arrow
                            Icon(
                                imageVector = Icons.Rounded.ArrowDropDown,
                                contentDescription = "Select AM/PM",
                                tint = accentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = meridianExpanded,
                        onDismissRequest = { meridianExpanded = false },
                        modifier = Modifier
                            .width(100.dp)
                            .background(unfocusedFieldColor)
                    ) {
                        listOf("AM", "PM").forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    onMeridianChange(option)
                                    meridianExpanded = false
                                },
                                text = {
                                    Text(
                                        text = option,
                                        color = textColor,
                                        fontFamily = karlaFont,
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BookingSummaryItemTeacher(
    image: Painter,
    label: String,
    value: String,
    textColor: Color = Color.White,
    accentColor: Color = Color(0xFF72F2EB)
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(
            painter = image,
            contentDescription = null,
            modifier = Modifier
                .size(22.dp)
                .aspectRatio(1f),
            tint = accentColor
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontFamily = karlaFont,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = value,
                fontFamily = karlaFont, // Changed from oswaldFont to match login screen
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
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
        faculty_id = requestList.facultyId,
        classroom_id = requestList.classroomId,
        reason = requestList.reason
    )

    return try {
        val response = client.from("requests").insert(insertionDataList) {
            select(columns = Columns.list("id"))
        }.decodeSingle<IdColumnVerify>()
        response.id
    } catch (e: Exception) {
        println("Error during slot booking request")
        null
    }
}