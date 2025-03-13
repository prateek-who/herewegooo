package com.example.herewegooo

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
) {
    val navItemList = when (userViewModel.userRole) {
        "teacher" -> listOf(
            NavItem(label = "Home", icon = Icons.Default.Home, forRoute = "Home"),
            NavItem(label = "Time Table", icon = Icons.Default.Menu, forRoute = "Timetable"),
            NavItem(label = "Profile", icon = Icons.Default.Person, forRoute = "Profile"),
        )
        "admin" -> listOf(
            NavItem(label = "Home", icon = Icons.Default.Home, forRoute = "adminPanel"),
            NavItem(label = "Profile", icon = Icons.Default.Person, forRoute = "Profile"),
        )
        else -> listOf(
            NavItem(label = "Home", icon = Icons.Default.Home, forRoute = "Home"),
            NavItem(label = "Time Table", icon = Icons.Default.Menu, forRoute = "Timetable"),
            NavItem(label = "Profile", icon = Icons.Default.Person, forRoute = "Profile"),
        )
    }

    // Get system insets
    val density = LocalDensity.current
    val windowInsets = WindowInsets.systemBars

    // Get IME (keyboard) visibility
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    // Calculate navigation bar height (system bottom bar)
    val systemNavBarHeight = with(density) {
        windowInsets.getBottom(this).toDp()
    }

    // Calculate our custom nav bar height
    val customNavBarHeight = if (imeVisible) 55.dp else 75.dp

    // Define colors based on role
    val accentColor = if (userViewModel.userRole == "admin") {
        Color(0xFFFF3A3A) // Vivid red for admin
    } else {
        Color(0xFF9676DB) // Purple for others
    }

    val backgroundColor = Color(0xFF1E1E26)

    // Ensure selectedIndex is within bounds
    val safeSelectedIndex = remember(selectedIndex, navItemList.size) {
        selectedIndex.coerceIn(0, navItemList.size - 1)
    }

    // Container that accounts for system insets
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(customNavBarHeight + systemNavBarHeight)
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom)),
        contentAlignment = Alignment.TopCenter
    ) {
        // Curved card for the navigation bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(customNavBarHeight)
                .padding(horizontal = 5.dp),
            shape = RoundedCornerShape(
                topStart = 25.dp,
                topEnd = 25.dp,
                bottomStart = 0.dp,
                bottomEnd = 0.dp
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItemList.forEachIndexed { index, item ->
                    val isSelected = index == safeSelectedIndex

                    // Animation values
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "scale"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if (index != safeSelectedIndex) {
                                    Log.d("BottomNavBar", "Clicked item: ${item.label} at index: $index")
                                    onItemSelected(index, item)
                                }
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        // Icon with animated scale
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(if (isSelected) 40.dp else 28.dp)
                                .scale(scale)
                        ) {
                            // Glow effect for selected items
                            if (isSelected) {
                                Canvas(modifier = Modifier.size(48.dp)) {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                accentColor.copy(alpha = 0.3f),
                                                accentColor.copy(alpha = 0f)
                                            )
                                        ),
                                        radius = size.minDimension / 1.5f
                                    )
                                }

                                // Selected icon with background
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .shadow(
                                            elevation = 8.dp,
                                            shape = CircleShape,
                                            spotColor = accentColor
                                        )
                                        .background(
                                            brush = Brush.linearGradient(
                                                colors = listOf(
                                                    accentColor,
                                                    accentColor.copy(alpha = 0.8f)
                                                ),
                                                start = Offset(0f, 0f),
                                                end = Offset(0f, 40f)
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.White
                                    )
                                }
                            } else {
                                // Unselected icon
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Animated text size
                        val textSize by animateFloatAsState(
                            targetValue = if (isSelected) 14f else 12f,
                            label = "textSize"
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = Color(0xFFF0F0F5),
                            fontFamily = funnelFont,
                            fontSize = textSize.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    // Display the content
    content()
}