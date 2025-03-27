package com.example.herewegooo

import android.provider.SyncStateContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
import com.example.herewegooo.network.FacultyList
import com.example.herewegooo.network.IdColumnVerify
import com.example.herewegooo.network.SwapRequest
import com.example.herewegooo.network.supabaseClient
import com.example.herewegooo.network.swapperList
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.launch
import java.time.LocalTime


@Composable
fun SwapClassDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    user: UserViewModel,
    onShowSnackbar: (message: String, type: SnackbarType) -> Unit,
    class_date: String,
    start_time: LocalTime,
    end_time: LocalTime,
    classroom_id: String
){
    val coroutineScope = rememberCoroutineScope()
    var facultyList by remember { mutableStateOf<List<FacultyList>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFaculty by remember { mutableStateOf<FacultyList?>(null) }
    var reason by remember { mutableStateOf("") }

    // Variables for dropdown
    var facultySearch by remember { mutableStateOf("") }
    var expandedDropdown by remember { mutableStateOf(false) }

    // Colors matching design
    val backgroundColor = Color(0xFF1E1E2E)    // Deep blue background
    val cardBackground = Color(0xFF272C3D)     // Slightly brighter card background
    val accentColor = Color(0xFF9676DB)        // Bright blue/purple accent
    val buttonColor = Color(0xFF16B1AC)        // Teal button color
    val textColor = Color(0xFFF8F8FF)          // Bright white text
    val highlightColor = Color(0xFF72F2EB)     // Teal highlight color
    val unfocusedFieldColor = Color(0xFF1E2239) // Darker field background
    val focusedFieldColor = Color(0xFF222845)   // Slightly lighter field background

    val client = supabaseClient()

    // Fetch faculty list when dialog opens
    LaunchedEffect(openDialog) {
        if (openDialog) {
            try {
                val teachers = getFacultyList(client)
                facultyList = teachers.filter { it.user_id != user.userId } // Filter out current user
                isLoading = false
            } catch (e: Exception) {
                onShowSnackbar("Failed to load faculty list: ${e.message}", SnackbarType.ERROR)
                isLoading = false
            }
        }
    }

    // Filter faculty based on search query
    val filteredFaculty = remember(facultyList, facultySearch) {
        if (facultySearch.isBlank()) {
            facultyList
        } else {
            facultyList.filter {
                it.username.contains(facultySearch, ignoreCase = true)
            }
        }
    }

    if (openDialog){
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
                    .heightIn(max = 540.dp)
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
                ) {
                    // Dialog head part
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardBackground)
                            .padding(vertical = 20.dp, horizontal = 24.dp)
                            .border(
                                width = 0.dp,
                                color = Color.Transparent,
                                shape = RectangleShape
                            ),
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
                                tint = buttonColor
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

                    // Main Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp)
                    ) {
                        // Faculty selection with dropdown
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "SELECT FACULTY",
                                color = textColor.copy(alpha = 0.7f),
                                fontFamily = karlaFont,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = accentColor)
                                }
                            } else {
                                // Faculty Dropdown
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = selectedFaculty?.username ?: facultySearch,
                                        onValueChange = { newText ->
                                            if (selectedFaculty == null || newText != selectedFaculty?.username) {
                                                facultySearch = newText
                                                selectedFaculty = null
                                                expandedDropdown = true
                                            }
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
                                                text = "Search or select faculty",
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
                                                painter = painterResource(id = R.drawable.person),
                                                contentDescription = "Faculty",
                                                tint = highlightColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                    )

                                    val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
                                    val scrollState = rememberScrollState()

                                    DropdownMenu(
                                        expanded = expandedDropdown && filteredFaculty.isNotEmpty() &&
                                                (!isKeyboardVisible || filteredFaculty.size <= 3),
                                        onDismissRequest = { expandedDropdown = false },
                                        modifier = Modifier
                                            .width(with(LocalDensity.current) {
                                                290.dp
                                            })
                                            .heightIn(max = 200.dp)
                                            .background(focusedFieldColor)
                                            .imePadding(),
                                        properties = PopupProperties(
                                            focusable = false,
                                            excludeFromSystemGesture = true
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 200.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .verticalScroll(scrollState)
                                            ) {
                                                if (filteredFaculty.isEmpty()) {
                                                    DropdownMenuItem(
                                                        onClick = { },
                                                        text = {
                                                            Text(
                                                                text = "No faculty found",
                                                                color = textColor.copy(alpha = 0.7f),
                                                                fontFamily = karlaFont
                                                            )
                                                        }
                                                    )
                                                } else {
                                                    filteredFaculty.forEach { faculty ->
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                selectedFaculty = faculty
                                                                facultySearch = faculty.username
                                                                expandedDropdown = false
                                                            },
                                                            text = {
                                                                Row(
                                                                    modifier = Modifier.fillMaxWidth(),
                                                                    verticalAlignment = Alignment.CenterVertically,
                                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                                ) {
                                                                    Text(
                                                                        text = faculty.username,
                                                                        color = textColor,
                                                                        fontFamily = karlaFont,
                                                                        fontSize = 16.sp,
                                                                        modifier = Modifier.weight(1f)
                                                                    )

                                                                    if (selectedFaculty?.user_id == faculty.user_id) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.Check,
                                                                            contentDescription = "Selected",
                                                                            tint = buttonColor,
                                                                            modifier = Modifier.size(20.dp)
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

                                // Display selected faculty if any
                                AnimatedVisibility(visible = selectedFaculty != null) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = cardBackground
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = buttonColor.copy(alpha = 0.6f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.person),
                                                    contentDescription = null,
                                                    tint = highlightColor,
                                                    modifier = Modifier.size(24.dp)
                                                )

                                                Column {
                                                    Text(
                                                        text = "Selected Faculty",
                                                        color = textColor.copy(alpha = 0.6f),
                                                        fontFamily = karlaFont,
                                                        fontSize = 12.sp
                                                    )

                                                    Text(
                                                        text = selectedFaculty?.username ?: "",
                                                        color = textColor,
                                                        fontFamily = karlaFont,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            IconButton(
                                                onClick = {
                                                    selectedFaculty = null
                                                    facultySearch = ""
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Clear Selection",
                                                    tint = textColor.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Reason field
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "REASON FOR SWAP",
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
                                        text = "Enter reason for swap request",
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
                            onClick = {
                                if (!isLoading){
                                    isLoading = true
                                    try {
                                        onDismiss()
                                    }finally {
                                        isLoading = false
                                    }
                                }
                                      },
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
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = textColor,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Cancel",
                                    fontFamily = karlaFont,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = {
                                if (selectedFaculty == null) {
                                    onShowSnackbar("Please select a faculty member", SnackbarType.ERROR)
                                } else if (reason.isBlank()) {
                                    onShowSnackbar("Please enter a reason for swap request", SnackbarType.ERROR)
                                } else {
                                    if (!isLoading){
                                        isLoading = true
                                    coroutineScope.launch {
                                        try {
                                            val swapRequest = swapperList(
                                                from_id = user.userId,
                                                to_id = selectedFaculty!!.user_id,
                                                class_date = class_date,
                                                start_time = start_time,
                                                end_time = end_time,
                                                classroom_id = classroom_id.toInt(),
                                                reason = reason,
                                                status = "pending",
                                            )

                                            swapFacultyRequest(client, swapRequest)
                                            onDismiss()
                                            onShowSnackbar(
                                                "Swap request sent successfully!",
                                                SnackbarType.SUCCESS
                                            )
                                        } catch (e: Exception) {
                                            onShowSnackbar(
                                                "Failed to send swap request: ${e.message}",
                                                SnackbarType.ERROR
                                            )
                                        } finally {
                                            isLoading = false
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
                            contentPadding = PaddingValues(0.dp),
                            enabled = !isLoading
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
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        color = textColor,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ){
                                    Icon(
                                        painter = painterResource(id = R.drawable.swap),
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Send Request",
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
}


// get faculty list from users
suspend fun getFacultyList(
    client: SupabaseClient
): List<FacultyList> {
    val faculty = client.from("users").select(columns = Columns.list("user_id, username")) {
        filter {
            eq("role", "teacher")
        }
    }.decodeList<FacultyList>()

    return faculty
}

// send this data to swap_requests
suspend fun swapFacultyRequest(
    client: SupabaseClient,
    swapRequest: swapperList
): Int? {

    val swapMaker = SwapRequest(
        from_id = swapRequest.from_id,
        to_id = swapRequest.to_id,
        class_date = swapRequest.class_date,
        start_time = swapRequest.start_time,
        end_time = swapRequest.end_time,
        classroom_id = swapRequest.classroom_id,
        reason = swapRequest.reason,
        status = swapRequest.status
    )

    return try {
        val response = client.from("swaprequest").insert(swapMaker){
            select(columns = Columns.list("id"))
        }.decodeSingle<IdColumnVerify>()
        response.id
    } catch (e: Exception) {
        println("Error in swapFacultyRequest: $e")
        throw e
        null
    }
}