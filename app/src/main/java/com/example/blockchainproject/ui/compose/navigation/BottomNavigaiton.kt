package com.example.blockchainproject.ui.compose.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "history")
    val icons = listOf(Icons.Default.Home, Icons.Default.List)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val baseGlassColor = Color(0xFFB2EBF2)

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(baseGlassColor)
    ) {
        Box(
            modifier = Modifier
                .height(54.dp)
        ) {
            NavigationBar(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Transparent)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF4444),
                                Color(0xFFFF8888),
                                Color(0xFFFF4444)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                items.forEachIndexed { index, screen ->
                    val selected = currentRoute == screen
                    val animatedScale by animateFloatAsState(
                        targetValue = if (selected) 2f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                        label = ""
                    )

                    NavigationBarItem(
                        icon = {
                            Box(
                                modifier = Modifier
                                    .height(54.dp),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Icon(
                                    icons[index],
                                    contentDescription = null,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            scaleX = animatedScale
                                            scaleY = animatedScale
                                        }
                                )
                            }
                        },
                        label = null,
                        selected = selected,
                        onClick = {
                            if (currentRoute != screen) {
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF4444),
                            selectedTextColor = Color(0xFFFF4444),
                            indicatorColor = Color.White.copy(alpha = 0.3f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        interactionSource = MutableInteractionSource(),
                    )
                }
            }
        }
    }

}