package com.example.herewegooo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MyDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    roomNumber: String,
    user: UserViewModel,
    onShowSnackbar: (String) -> Unit
) {
    val snackbarType = remember { mutableStateOf(SnackbarType.SUCCESS) }
    val coroutineScope = rememberCoroutineScope()

    var expanded by remember { mutableStateOf(false) }

    val todayKey = LocalDate.now().format(DateTimeFormatter.ISO_DATE)
    val dateOptions = remember(todayKey) {
        // Recomputes only when todayKey changes (daily)
        (0 until 10).map { offset ->
            LocalDate.now()
                .plusDays(offset.toLong())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        }
    }
    var selectedDate by remember { mutableStateOf(dateOptions.firstOrNull() ?: "Select Date") }

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
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF131315),
                modifier = Modifier
                    .padding(16.dp)
                    .width(400.dp)
                    .height(750.dp)
            ) {
                Column(modifier = Modifier.padding(0.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFF1F2933))
                    ) {
                        Text(
                            text = "Book your slot",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 28.sp,
                            fontFamily = oswaldFont,
                            color = Color.White
                        )
                    }

                    Column {
                        InfoField(
                            label = "Room No",
                            value = roomNumber,
                            modifier = Modifier
                                .offset(x = 15.dp, y = 10.dp)
                                .width(260.dp)
                                .height(70.dp)
                        )

                        Box(
                            modifier = Modifier
                                .offset(x = 15.dp, y = 21.dp)
                                .width(260.dp)
                                .height(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1C1C1E))
//                                .clickable { expanded = true } // Toggle dropdown on click
                        ) {
                            // Display a label and the currently selected date
                            Text(
                                text = "Date",
                                fontFamily = karlaFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.offset(x = 10.dp, y = 5.dp),
                                color = Color(0xFF77767b)
                            )
                            Text(
                                text = selectedDate,
                                fontFamily = funnelFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 30.sp,
                                modifier = Modifier.offset(x = 10.dp, y = 25.dp),
                                color = Color(0xFFFAFAFA)
                            )

                            Box(
                                modifier = Modifier
                                    .offset(x = 10.dp, y = 70.dp)
//                                .clip(RoundedCornerShape(10.dp))
                            ) {
                                DropdownMenu(
                                    modifier = Modifier
                                        .width(240.dp)
//                                    .offset(20.dp)
                                        .background(Color(0xFF1C1C1E))
                                        .align(Alignment.Center),
                                    expanded = expanded && dateOptions.isNotEmpty(),
                                    onDismissRequest = { expanded = false }
                                ) {
                                    val itemHeight = 48.dp // Approximate height of DropdownMenuItem
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
                                                            fontSize = 20.sp,
                                                            modifier = Modifier.offset(x = 60.dp)
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Button(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .offset(x = 110.dp, y = 5.dp)
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
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .offset(x = 15.dp, y = 32.dp)
                                .width(260.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1C1C1E))
                        ) {
                            // Display a label and the currently selected date
                            Text(
                                text = "From",
                                fontFamily = karlaFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.offset(x = 10.dp, y = 5.dp),
                                color = Color(0xFF77767b)
                            )
                            Row {
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(55.dp)
                                ) {
                                    OutlinedTextField(
                                        value = fromHourEntry,
                                        onValueChange = { newValue ->
                                            // Allow if the input is empty or only contains digits
                                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                                fromHourEntry = newValue

                                                // Optionally, only validate if exactly 2 digits are entered:
                                                if (newValue.length == 2) {
                                                    val intVal = newValue.toIntOrNull()
                                                    if (intVal == null || intVal !in 1 until 13) {
                                                        fromHourEntry = ""
                                                    }
                                                }
                                            }
                                        },
                                        placeholder = {
                                            Text(
                                                text = "HH",
                                                fontFamily = funnelFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                color = Color(0xFF77767b)
                                            )
                                        },
                                        textStyle = TextStyle(
                                            fontFamily = funnelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp,
                                            color = Color(0xFFFAFAFA)
                                        ),
                                        modifier = Modifier.offset(x = 10.dp, y = 25.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledPlaceholderColor = Color(0xFF77767b),
                                            unfocusedPlaceholderColor = Color(0xFF77767b),
                                            focusedPlaceholderColor = Color.Black,
                                            focusedTextColor = Color(0xFFFAFAFA),
                                            unfocusedTextColor = Color(0xFFFAFAFA),
                                            cursorColor = Color(0xFF77767b),
                                            focusedBorderColor = Color(0xFF77767b)
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        )
                                    )
                                }
                                Text(
                                    text = ":",
                                    fontFamily = funnelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 40.sp,
                                    color = Color.White,
                                    modifier = Modifier.offset(x = 13.dp, y = 25.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(55.dp)
                                        .offset(x = 5.dp)
                                ) {
                                    OutlinedTextField(
                                        value = fromMinuteEntry,
                                        onValueChange = { newValue ->
                                            // Allow if the input is empty or only contains digits
                                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                                fromMinuteEntry = newValue

                                                // Optionally, only validate if exactly 2 digits are entered:
                                                if (newValue.length == 2) {
                                                    val intVal = newValue.toIntOrNull()
                                                    if (intVal == null || intVal !in 0 until 60) {
                                                        fromMinuteEntry = ""
                                                    }
                                                }
                                            }
                                        },
                                        placeholder = {
                                            Text(
                                                text = "MM",
                                                fontFamily = funnelFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                color = Color(0xFF77767b)
                                            )
                                        },
                                        textStyle = TextStyle(
                                            fontFamily = funnelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp,
                                            color = Color(0xFFFAFAFA)
                                        ),
                                        modifier = Modifier.offset(x = 10.dp, y = 25.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledPlaceholderColor = Color(0xFF77767b),
                                            unfocusedPlaceholderColor = Color(0xFF77767b),
                                            focusedPlaceholderColor = Color.Black,
                                            focusedTextColor = Color(0xFFFAFAFA),
                                            unfocusedTextColor = Color(0xFFFAFAFA),
                                            cursorColor = Color(0xFF77767b),
                                            focusedBorderColor = Color(0xFF77767b)
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        )
                                    )
                                }
                                Text(
                                    text = ":",
                                    fontFamily = funnelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 40.sp,
                                    color = Color.White,
                                    modifier = Modifier.offset(x = 18.dp, y = 25.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(55.dp)
                                        .offset(x = 19.dp, y = 25.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF77767b),
                                            shape = RoundedCornerShape(5.dp)
                                        )

                                ) {
                                    var meridianExpanded by remember { mutableStateOf(false) }
                                    Text(
                                        text = fromMeridianEntry,
                                        fontFamily = funnelFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        modifier = Modifier
//                                            .offset(x = 15.dp, y = 25.dp)
                                            .clickable { meridianExpanded = true }
                                            .align(Alignment.Center),
                                        color = Color(0xFFFAFAFA)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .offset(x = 7.dp, y = 65.dp)
                                    ) {
                                        val itemHeight =
                                            48.dp // Approximate height of DropdownMenuItem
                                        val maxHeight = itemHeight * 2
                                        DropdownMenu(
                                            modifier = Modifier
                                                .width(70.dp)
                                                .background(Color(0xFF1C1C1E))
                                                .align(Alignment.Center),
                                            expanded = meridianExpanded,
                                            onDismissRequest = { meridianExpanded = false }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .height(maxHeight)
                                                    .verticalScroll(rememberScrollState())
                                            ) {
                                                Column {
                                                    listOf("AM", "PM").forEach { timePeriod ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                fromMeridianEntry = timePeriod
                                                                meridianExpanded = false
                                                            },
                                                            text = {
                                                                Text(
                                                                    text = timePeriod,
                                                                    color = Color.White,
                                                                    fontFamily = funnelFont,
                                                                    fontSize = 20.sp,
                                                                    modifier = Modifier.offset(x = 8.dp)
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
                        }

                        Box(
                            modifier = Modifier
                                .offset(x = 15.dp, y = 43.dp)
                                .width(260.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF1C1C1E))
                        ) {
                            // Display a label and the currently selected date
                            Text(
                                text = "To",
                                fontFamily = karlaFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.offset(x = 10.dp, y = 5.dp),
                                color = Color(0xFF77767b)
                            )
                            Row {
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(55.dp)
                                ) {
                                    OutlinedTextField(
                                        value = toHourEntry,
                                        onValueChange = { newValue ->
                                            // Allow if the input is empty or only contains digits
                                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                                toHourEntry = newValue

                                                // Optionally, only validate if exactly 2 digits are entered:
                                                if (newValue.length == 2) {
                                                    val intVal = newValue.toIntOrNull()
                                                    if (intVal == null || intVal !in 1 until 13) {
                                                        toHourEntry = ""
                                                    }
                                                }
                                            }
                                        },
                                        placeholder = {
                                            Text(
                                                text = "HH",
                                                fontFamily = funnelFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                color = Color(0xFF77767b)
                                            )
                                        },
                                        textStyle = TextStyle(
                                            fontFamily = funnelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp,
                                            color = Color(0xFFFAFAFA)
                                        ),
                                        modifier = Modifier.offset(x = 10.dp, y = 25.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledPlaceholderColor = Color(0xFF77767b),
                                            unfocusedPlaceholderColor = Color(0xFF77767b),
                                            focusedPlaceholderColor = Color.Black,
                                            focusedTextColor = Color(0xFFFAFAFA),
                                            unfocusedTextColor = Color(0xFFFAFAFA),
                                            cursorColor = Color(0xFF77767b),
                                            focusedBorderColor = Color(0xFF77767b)
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        )
                                    )
                                }
                                Text(
                                    text = ":",
                                    fontFamily = funnelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 40.sp,
                                    color = Color.White,
                                    modifier = Modifier.offset(x = 13.dp, y = 25.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(55.dp)
                                        .offset(x = 5.dp)
                                ) {
                                    OutlinedTextField(
                                        value = toMinuteEntry,
                                        onValueChange = { newValue ->
                                            // Allow if the input is empty or only contains digits
                                            if (newValue.length <= 2 && newValue.all { it.isDigit() }) {
                                                toMinuteEntry = newValue

                                                // Optionally, only validate if exactly 2 digits are entered:
                                                if (newValue.length == 2) {
                                                    val intVal = newValue.toIntOrNull()
                                                    if (intVal == null || intVal !in 0 until 60) {
                                                        toMinuteEntry = ""
                                                    }
                                                }
                                            }
                                        },
                                        placeholder = {
                                            Text(
                                                text = "MM",
                                                fontFamily = funnelFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 24.sp,
                                                color = Color(0xFF77767b)
                                            )
                                        },
                                        textStyle = TextStyle(
                                            fontFamily = funnelFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 25.sp,
                                            color = Color(0xFFFAFAFA)
                                        ),
                                        modifier = Modifier.offset(x = 10.dp, y = 25.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledPlaceholderColor = Color(0xFF77767b),
                                            unfocusedPlaceholderColor = Color(0xFF77767b),
                                            focusedPlaceholderColor = Color.Black,
                                            focusedTextColor = Color(0xFFFAFAFA),
                                            unfocusedTextColor = Color(0xFFFAFAFA),
                                            cursorColor = Color(0xFF77767b),
                                            focusedBorderColor = Color(0xFF77767b)
                                        ),
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            keyboardType = KeyboardType.Number
                                        )
                                    )
                                }
                                Text(
                                    text = ":",
                                    fontFamily = funnelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 40.sp,
                                    color = Color.White,
                                    modifier = Modifier.offset(x = 18.dp, y = 25.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(55.dp)
                                        .offset(x = 19.dp, y = 25.dp)
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFF77767b),
                                            shape = RoundedCornerShape(5.dp)
                                        )

                                ) {
                                    var meridianExpanded by remember { mutableStateOf(false) }
                                    Text(
                                        text = toMeridianEntry,
                                        fontFamily = funnelFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp,
                                        modifier = Modifier
//                                            .offset(x = 15.dp, y = 25.dp)
                                            .clickable { meridianExpanded = true }
                                            .align(Alignment.Center),
                                        color = Color(0xFFFAFAFA)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .offset(x = 7.dp, y = 65.dp)
                                    ) {
                                        val itemHeight =
                                            48.dp // Approximate height of DropdownMenuItem
                                        val maxHeight = itemHeight * 2
                                        DropdownMenu(
                                            modifier = Modifier
                                                .width(70.dp)
                                                .background(Color(0xFF1C1C1E))
                                                .align(Alignment.Center),
                                            expanded = meridianExpanded,
                                            onDismissRequest = { meridianExpanded = false }
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .height(maxHeight)
                                                    .verticalScroll(rememberScrollState())
                                            ) {
                                                Column {
                                                    listOf("AM", "PM").forEach { timePeriod ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                toMeridianEntry = timePeriod
                                                                meridianExpanded = false
                                                            },
                                                            text = {
                                                                Text(
                                                                    text = timePeriod,
                                                                    color = Color.White,
                                                                    fontFamily = funnelFont,
                                                                    fontSize = 20.sp,
                                                                    modifier = Modifier.offset(x = 8.dp)
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
                        }

                        InfoField(
                            label = "Faculty",
                            value = user.userName,
                            modifier = Modifier
                                .offset(x = 15.dp, y = 54.dp)
                                .width(260.dp)
                                .height(70.dp)
                        )

                        Box(modifier = Modifier
                            .width(260.dp)
                            .height(150.dp)
                            .offset(x = 15.dp, y = 65.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1C1C1E))
                        ) {
                            Text(
                                text = "Reason",
                                fontFamily = karlaFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.offset(x = 10.dp, y = 5.dp),
                                color = Color(0xFF77767b)
                            )
                            OutlinedTextField(
                                value = reason,
                                onValueChange = { newValue ->
                                    reason = newValue
                                },
                                placeholder = {
                                    Text(
                                        text = "Enter reason for booking slot",
                                        fontFamily = funnelFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF77767b)
                                    )
                                },
                                textStyle = TextStyle(
                                    fontFamily = funnelFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFFAFAFA)
                                ),
                                modifier = Modifier
                                    .offset(x = 10.dp, y = 25.dp)
                                    .width(240.dp)
                                    .height(115.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF77767b),
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledPlaceholderColor = Color(0xFF77767b),
                                    unfocusedPlaceholderColor = Color(0xFF77767b),
                                    focusedPlaceholderColor = Color.Black,
                                    focusedTextColor = Color(0xFFFAFAFA),
                                    unfocusedTextColor = Color(0xFFFAFAFA),
                                    cursorColor = Color(0xFF77767b),
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .offset(y = 80.dp)
                    ) {
                        Button(
                            onClick = {
                                val fromHour = fromHourEntry.toIntOrNull()
                                val fromMinute = fromMinuteEntry.toIntOrNull()
                                val toHour = toHourEntry.toIntOrNull()
                                val toMinute = toMinuteEntry.toIntOrNull()


                                if(fromHour == null || fromMinute == null || toHour == null || toMinute == null){
                                    println("Invalid input!")
                                }else {
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
                                    val finalDate = bookingDate.format(outputFormatter) // yyyy-MM-dd format

                                    val today = LocalDate.now()

                                    val startTime = LocalTime.of(fromHour, fromMinute)
                                    val endTime = LocalTime.of(toHour, toMinute)


                                    if (bookingDate.isBefore(today)) {
                                        println("Booking date is in the past!")
                                    } else if (bookingDate.isEqual(today)) {
                                        if (fromTotal < currentTotal) {
                                            println("Booking start time is in the past!")
                                        } else if (toTotal < fromTotal) {
                                            println("Booking time mismatch!")
                                        } else {
                                            println("Times are valid, proceed with booking.")

                                            println("Date format being sent: $finalDate")
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
                                                println(success)
                                                onDismiss()
                                                if (success != null) {
                                                    onShowSnackbar("Slot Requested!")
                                                }else{
                                                    onShowSnackbar("Error booking slot!")
                                                }
                                            }
                                        }
                                    }else{
                                        if (toTotal < fromTotal) {
                                            println("Booking time mismatch!")
                                        } else {

                                            println("Times are valid, proceed with booking.")
                                            coroutineScope.launch {
                                                val success = slotBookingRequest(client, sendRequest(finalDate, startTime, endTime, user.userName, roomNumber.toInt(), reason))
                                                println(success)
                                                onDismiss()
                                                if (success != null) {
                                                    snackbarType.value = SnackbarType.SUCCESS
                                                    onShowSnackbar("Slot Requested!")
                                                }else{
                                                    snackbarType.value = SnackbarType.ERROR
                                                    onShowSnackbar("Error booking slot!")
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(120.dp)
                                .height(50.dp)
                                .offset(x = 14.dp, y = 0.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B9543)
                            )
                        ) {
                            Text(
                                text = "Book Slot",
                                fontFamily = oswaldFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(125.dp)
                                .height(50.dp)
                                .offset(x = 30.dp, y = 0.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB31212)
                            )
                        ) {
                            Text(
                                text = "Close",
                                fontFamily = oswaldFont,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    labelFontSize: TextUnit = 12.sp,
    valueFontSize: TextUnit = 30.sp,
    containerColor: Color = Color(0xFF1C1C1E),
    shape: Shape = RoundedCornerShape(10.dp)
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
    ) {
        Text(
            text = label,
            fontFamily = karlaFont,
            fontWeight = FontWeight.Bold,
            fontSize = labelFontSize,
            modifier = Modifier.offset(x = 10.dp, y = 5.dp),
            color = Color(0xFF77767b)
        )
        Text(
            text = value,
            fontFamily = funnelFont,
            fontWeight = FontWeight.Bold,
            fontSize = valueFontSize,
            modifier = Modifier.offset(x = 10.dp, y = 25.dp),
            color = Color(0xFFFAFAFA)
        )

    }
}


suspend fun slotBookingRequest(
    client: SupabaseClient,
    requestList: sendRequest
    ): Int?{
    val insertionDataList = Request(class_date = requestList.classDate, start_time = requestList.startTime, end_time = requestList.endTime, faculty_name = requestList.facultyName, classroom_id = requestList.classroomId, reason = requestList.reason)

    return try {
        val response = client.from("requests").insert(insertionDataList) {
            select(columns = Columns.list("id"))
        }.decodeSingle<IdColumnVerify>()
        response.id
    }catch (e: Exception){
        println("Error during slot booking request ${e.localizedMessage}")
        null
    }
}