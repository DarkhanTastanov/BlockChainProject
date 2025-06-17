package com.example.blockchainproject.ui.compose.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.blockchainproject.ui.compose.screen.SamsungColorScheme

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "history")
    val icons = listOf(Icons.Default.Home, Icons.Default.List)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val baseGlassColor = SamsungColorScheme.background

    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(baseGlassColor)
            .border(2.dp, SamsungColorScheme.primary, RoundedCornerShape(12.dp))
            .height(60.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, screen ->
                val selected = currentRoute == screen
                val animatedScale by animateFloatAsState(
                    targetValue = if (selected) 2f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                    label = ""
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selected) Color.White.copy(0.1f) else Color.Transparent)
                        .clickable {
                            if (currentRoute != screen) {
                                navController.navigate(screen) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = null,
                        tint = if (selected) SamsungColorScheme.primary else Color.Gray,
                        modifier = Modifier.graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                        }
                    )
                }
            }
        }
    }
}
