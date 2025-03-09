package com.example.herewegooo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun EightFloor(navController: NavController, floor: String){
    val gradientColorChoice = listOf(
        Color(0xFF121218),
        Color(0xFF121218),
//        Color(0xFF0C3B2E),
//        Color(0xFF0A4B38),
    )

    val gradientBackground = Brush.verticalGradient(
        colors = gradientColorChoice,
        startY = 0f,
        endY = Float.POSITIVE_INFINITY
    )

    val roomFont = oswaldFont

    val roomNumbers = mutableListOf(
        "801", "802", "803", "804","805", "806",
        "807", "808", "809", "810","811", "812"
    )

    val actualRooms = mutableListOf<String>()

    for(room in roomNumbers){
        val newRoom = floor + room.substring(1)
        actualRooms.add(newRoom)
    }

    Column (
        modifier = Modifier.fillMaxSize()
            .background(brush = gradientBackground)
    ){
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
                        .height(100.dp)
                        .width(350.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ){
                    Text(text = label,
                        fontSize = 30.sp,
                        fontFamily = roomFont,
                        color = Color(0xFFF0F0F5)
                        )
                }
            }
        }
    }
}