package com.example.herewegooo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun MyDialog(
    openDialog: Boolean,
    onDismiss: () -> Unit,
    roomNumber: String
) {
    if (openDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp)
                    .height(500.dp)
            ) {
                Column(modifier = Modifier.padding(0.dp)) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(Color(0xFF1F2933))
                    ){
                        Text(
                            text = "Book your slot",
                            modifier = Modifier.align(Alignment.Center),
                            fontSize = 28.sp,
                            fontFamily = oswaldFont,
                            color = Color.White
                        )
                    }

                    Box(){
                        Text(
                            text = "Room No: $roomNumber"
                        )

                        Text(
                            text = "Room No: $roomNumber"
                        )

                    }
                    Spacer(modifier = Modifier.height(18.dp))


                    Row {
                        Button(onClick = onDismiss) {
                            Text("Book")
                        }

                        HorizontalDivider(modifier = Modifier.width(20.dp))

                        Button(onClick = onDismiss) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}
