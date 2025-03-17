package com.example.herewegooo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.delay

val staticRoomList: List<String> = (3..9).flatMap { floor ->
    (1..24).map { room ->
        "%d%02d".format(floor, room)
    }
}


@Composable
fun MainScreen(navController: NavController) {
    val focusManager = LocalFocusManager.current

    // Color scheme to match Login screen
    val backgroundColor = Color(0xFF1E1E2E)  // Deep blue background
    val textColor = Color(0xFFF8F8FF)         // Bright white text
    val accentColor = Color(0xFF9676DB)       // Bright blue accent
    val buttonColor = Color(0xFF16B1AC)       // Teal button color
    val cardBackground = Color(0xFF272C3D)    // Slightly brighter card background

    // Field colors
    val unfocusedFieldColor = Color(0xFF1E2239)  // Darker field
    val focusedFieldColor = Color(0xFF222845)    // Slightly lighter when focused

    // Font and styling
    val floorFont = oswaldFont
    val floorFontSize = 36.sp
    val btnShape = RoundedCornerShape(16.dp)

    var searchQuery by remember { mutableStateOf("") }
    val roomList by remember { mutableStateOf(staticRoomList) }

    val filteredRooms by remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isEmpty()) {
                emptyList()
            } else {
                roomList.filter { it.startsWith(searchQuery) }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Simple title instead of logo
            Text(
                text = "Building Directory",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = karlaFont,
                color = textColor,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Refined search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() }) {
                        searchQuery = newValue
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(58.dp),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = unfocusedFieldColor,
                    focusedContainerColor = focusedFieldColor,
                    unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                    focusedBorderColor = accentColor,
                    unfocusedTextColor = textColor,
                    focusedTextColor = textColor,
                    cursorColor = accentColor
                ),
                textStyle = TextStyle(
                    fontFamily = karlaFont,
                    fontSize = 24.sp,
                    color = textColor
                ),
                placeholder = {
                    Text(
                        text = "Search room number",
                        color = Color(0xFF79797F),
                        fontFamily = karlaFont,
                        fontSize = 18.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = accentColor,
                        modifier = Modifier.size(24.dp)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true
            )

            // Dropdown for search results
            if (searchQuery.isNotEmpty() && filteredRooms.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .zIndex(1f)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF242432)
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFF3C3C3E)
                        )
                    ) {
                        val scrollState = rememberScrollState()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                        ) {
                            filteredRooms.forEach { room ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            navController.navigate("floorNumber/${room[0]}/roomNumber/$room")
                                        }
                                        .padding(vertical = 12.dp, horizontal = 24.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = room,
                                        color = textColor,
                                        fontFamily = funnelFont,
                                        fontSize = 18.sp
                                    )
                                }
                                if (filteredRooms.indexOf(room) < filteredRooms.size - 1) {
                                    HorizontalDivider(
                                        color = Color(0xFF3C3C3E),
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Floors section with consistent design
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val floors = listOf(
                    Pair("Ground Floor", -1),
                    Pair("1st Floor", 1),
                    Pair("2nd Floor", 2),
                    Pair("3rd Floor", 3),
                    Pair("4th Floor", 4),
                    Pair("5th Floor", 5),
                    Pair("6th Floor", 6),
                    Pair("7th Floor", 7),
                    Pair("8th Floor", 8),
                    Pair("9th Floor", 9)
                )

                floors.forEach { (floorName, floorNumber) ->
                    Button(
                        onClick = {
                            if (floorNumber >= 3) {
                                navController.navigate("floorNumber/$floorNumber")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = btnShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = textColor
                        ),
                        border = BorderStroke(
                            width = 0.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    accentColor.copy(alpha = 0.7f),
                                    accentColor.copy(alpha = 0.7f)
                                )
                            )
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            cardBackground,
                                            cardBackground.copy(alpha = 0.8f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = floorName,
                                fontSize = floorFontSize,
                                fontFamily = floorFont,
                                color = textColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
