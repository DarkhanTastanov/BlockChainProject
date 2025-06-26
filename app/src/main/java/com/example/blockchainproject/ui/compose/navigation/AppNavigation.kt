package com.example.blockchainproject.ui.compose.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.blockchainproject.ui.compose.screen.*

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    GlassContainer(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    top = 0.dp,
                    start = 0.dp,
                    end = 0.dp,
                    bottom = 0.dp
                )
            ),
//            .windowInsetsPadding(WindowInsets.systemBars),
        content = {
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.fillMaxSize()
            ) {
                composable("login") {
                    LoginScreen(navController)
                }
                composable("home") {
                    HomeScreen(onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    })
                }
                composable("history") {
                    HistoryScreen(navController)
                }
                composable(
                    "transaction_details/{address}/{hash}",
                    arguments = listOf(
                        navArgument("address") { type = NavType.StringType },
                        navArgument("hash") { type = NavType.StringType }
                    )
                ) {
                    val hash = it.arguments?.getString("hash") ?: ""
                    val address = it.arguments?.getString("address") ?: ""
                    TransactionDetailsScreen(hash, address)
                }
            }
        },
        glassContent = {
            if (currentRoute != "login") {
                GlassBox(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
//                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .padding(
                            PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                            )
                        )
                        .height(60.dp),
                    blur = 0.2f,
                    scale = 0.2f,
                    tint = SamsungColorScheme.primary.copy(alpha = 0.5f),
                    darkness = 0.1f,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    BottomNavigationRow(navController)
                }
            }
        }

    )
}

private fun getEnterTransition(initial: NavBackStackEntry, target: NavBackStackEntry): EnterTransition {
    return when {
        target.destination.route?.startsWith("transaction_details") == true -> {
            scaleIn(initialScale = 0.8f, animationSpec = tween(500)) + fadeIn(animationSpec = tween(500))
        }
        initial.destination.route == "home" && target.destination.route == "history" -> {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(500))
        }
        initial.destination.route == "history" && target.destination.route == "home" -> {
            slideInHorizontally(initialOffsetX = { -it }) + fadeIn(animationSpec = tween(500))
        }
        else -> fadeIn(animationSpec = tween(500))
    }
}

private fun getExitTransition(initial: NavBackStackEntry, target: NavBackStackEntry): ExitTransition {
    return when {
        initial.destination.route?.startsWith("transaction_details") == true -> {
            scaleOut(targetScale = 0.8f, animationSpec = tween(500)) + fadeOut(animationSpec = tween(500))
        }
        initial.destination.route == "home" && target.destination.route == "history" -> {
            slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(animationSpec = tween(500))
        }
        initial.destination.route == "history" && target.destination.route == "home" -> {
            slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(500))
        }
        else -> fadeOut(animationSpec = tween(500))
    }
}


private fun getPopEnterTransition(initial: NavBackStackEntry, target: NavBackStackEntry): EnterTransition {
    return getEnterTransition(initial, target)
}

private fun getPopExitTransition(initial: NavBackStackEntry, target: NavBackStackEntry): ExitTransition {
    return getExitTransition(initial, target)
}
