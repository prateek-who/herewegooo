package com.example.herewegooo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.herewegooo.data.model.UserViewModel


@Composable
fun BottomNavBar(
    userViewModel: UserViewModel,
    selectedIndex: Int,
    onItemSelected: (Int, NavItem) -> Unit,
    content: @Composable () -> Unit
){
    println("Role: ${userViewModel.userRole}, Name: ${userViewModel.userName}")
    val navItemList = if (userViewModel.userRole == "teacher") {
        listOf(
            NavItem(label = "Home", icon = Icons.Default.Home, forRoute = "Home"),
            NavItem(label = "Time Table", icon = Icons.Default.Menu, forRoute = "Timetable"),
            NavItem(label = "Profile", icon = Icons.Default.Person, forRoute = "Profile"),
        )
    } else if (userViewModel.userRole == "admin"){
        listOf(
            NavItem(label = "Home", icon = Icons.Default.Home, forRoute = "adminPanel"),
        )
    } else {
        listOf(
            NavItem(label = "Home", icon = Icons.Default.Home, forRoute = "Home"),
            NavItem(label = "Time Table", icon = Icons.Default.Menu, forRoute = "Timetable"),
        )
    }


    val density = LocalDensity.current
    val imeVisible = with(density) {
        WindowInsets.ime.getBottom(this).toDp()
    }
    val navHeight = if (imeVisible > 0.dp) {
        60.dp
    }else {110.dp}

    NavigationBar(
        modifier = Modifier.height(navHeight),
        containerColor =
            if (userViewModel.userRole == "admin") {
                Color(0xFF1E1E26) // admin panel color
            }else{
                Color(0xFF1E1E26) // Everybody else uses this color
            },
        tonalElevation = 5.dp
    ) {
        navItemList.forEachIndexed { index, navItem ->
            val isSelected = (selectedIndex == index)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index, navItem) },
                icon = {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .offset(y = 5.dp)
                                .background(
                                    if (userViewModel.userRole == "admin") Color(0xFFFF3A3A) else Color(0xFF4F6BFF),
                                    shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = navItem.label,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = navItem.label,
                            modifier = Modifier
                                .size(28.dp)
                                .offset(y = 7.dp),
                            tint = Color.White
                        )
                    }
                },
                label = {
                    if (isSelected) {
                        Text(
                            text = navItem.label,
                            color = Color(0xFFF0F0F5),
                            fontFamily = funnelFont,
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = (-5).dp)
                        )
                    } else {
                        Text(
                            text = navItem.label,
                            color = Color(0xFFF0F0F5),
                            fontFamily = funnelFont,
                            fontSize = 12.sp,
                            modifier = Modifier.offset(y = 0.dp)
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedIconColor = Color.Transparent,
                    unselectedTextColor = Color.White,
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Green,
                    indicatorColor = Color.Transparent,
                    disabledIconColor = Color.White
                ),
                modifier = if (isSelected) {
                    if (userViewModel.userRole == "admin"){
                        Modifier.background(Color(0xFF1C1C1E))
                    }
                    else {
                        Modifier.background(Color(0xFF121218))
                    }
                } else {
                    Modifier.background(Color(0xFF1E1E26))
                }
            )
        }
    }
}