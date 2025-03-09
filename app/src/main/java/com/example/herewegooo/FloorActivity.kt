package com.example.herewegooo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.PopupProperties
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
    val btnColor = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
    )
    val buttonTextColor = Color(0xFFF0F0F5)

    val gradientColorChoice = listOf(
        Color(0xFF121218),
        Color(0xFF121218),
//        Color(0xFF6D9773),
    )
    val gradientBackground = Brush.verticalGradient(
        colors = gradientColorChoice,
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    val floorFont = oswaldFont
    val floorFontSize = 40.sp

    val btnShape = RoundedCornerShape(25.dp)

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
            .background(brush = gradientBackground)
            .clickable (
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Box(
                modifier = Modifier
                    .width(500.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() }) {
                            searchQuery = newValue
                            expanded = newValue.isNotEmpty()
                        }
                    },
                    modifier = Modifier
                        .height(55.dp)
                        .width(350.dp)
                        .offset(x = 20.dp, y = (-20).dp)
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(50.dp),
                    placeholder = {
                        Text(
                            text = "Search your room",
                            color = Color(0xFF79797F),
                            fontSize = 20.sp,
                            fontFamily = floorFont
                        )
                    },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 20.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedIndicatorColor = Color(0xFF79797F),
                        focusedIndicatorColor = Color.White,
                        unfocusedTextColor = buttonTextColor
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )

                Box(modifier = Modifier.offset(x = 55.dp, y = 40.dp)) {
                    DropdownMenu(
                        expanded = expanded && filteredRooms.isNotEmpty(),
                        onDismissRequest = { expanded = false },
                        properties = PopupProperties(focusable = false),
                        modifier = Modifier
                            .width(280.dp)
//                            .align(Alignment.Center)
//                            .offset(x = 30.dp)
                            .background(Color(0xFF242432))
                            .border(width = 2.dp, color = Color(0xFF3C3C3E))
                    ) {
                        // Scrollable Column with constrained height
                        val itemHeight = 48.dp // Approximate height of DropdownMenuItem
                        val maxHeight = 250.dp
                        val totalHeight = (filteredRooms.size * itemHeight).coerceAtMost(maxHeight)
                        Box(
                            modifier = Modifier
                                .height(totalHeight)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Column {
                                filteredRooms.forEach { room ->
                                    DropdownMenuItem(
                                        onClick = {
                                            navController.navigate("floorNumber/${room[0]}/roomNumber/$room")
                                            expanded = false
                                        },
                                        text = {
                                            Text(
                                                text = room,
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
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(brush = gradientBackground)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor,
                    //            border = BorderStroke(2.dp, color = Color(0xFF92b3ba))
                ) {
                    Text(
                        text = "Ground Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "1st Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "2nd Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/3")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "3rd Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/4")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "4th Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/5")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "5th Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/6")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "6th Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/7")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "7th Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/8")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "8th Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Button(
                    onClick = {
                        navController.navigate("floorNumber/9")
                    },
                    modifier = Modifier
                        .height(115.dp)
                        .width(350.dp)
                        .offset(x = 20.dp),
                    shape = btnShape,
                    colors = btnColor
                ) {
                    Text(
                        text = "9th Floor",
                        fontSize = floorFontSize,
                        fontFamily = floorFont,
                        color = buttonTextColor
                    )
                }
                Spacer(modifier = Modifier.height(45.dp))
            }
        }
    }
}
