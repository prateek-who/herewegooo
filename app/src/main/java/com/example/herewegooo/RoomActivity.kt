package com.example.herewegooo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun EightFloor(navController: NavController, floor: String) {
    // Color scheme to match the rest of the app
    val backgroundColor = Color(0xFF1E1E2E)  // Deep blue background
    val textColor = Color(0xFFF8F8FF)       // Bright white text
    val accentColor = Color(0xFF9676DB)     // Bright blue accent
    val buttonColor = Color(0xFF16B1AC)     // Teal button color
    val cardBackground = Color(0xFF272C3D)  // Slightly brighter card background

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(backgroundColor, backgroundColor),
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    val roomFont = oswaldFont
    val btnShape = RoundedCornerShape(16.dp)

    val roomNumbers = mutableListOf(
        "801", "802", "803", "804", "805", "806",
        "807", "808", "809", "810", "811", "812",
        "813", "814", "815", "816", "817", "818",
        "819", "820", "821", "822", "823", "824",
    )

    val actualRooms = mutableListOf<String>()

    for (room in roomNumbers) {
        val newRoom = floor + room.substring(1)
        actualRooms.add(newRoom)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(actualRooms) { label ->
                Button(
                    onClick = {
                        navController.navigate("floorNumber/$floor/roomNumber/$label")
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .height(100.dp),
                    shape = btnShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = textColor
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                buttonColor.copy(alpha = 0.7f),
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
                            text = label,
                            fontSize = 30.sp,
                            fontFamily = roomFont,
                            color = textColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}