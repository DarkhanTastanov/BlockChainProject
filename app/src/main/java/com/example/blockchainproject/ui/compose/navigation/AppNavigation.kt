package com.example.blockchainproject.ui.compose.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.blockchainproject.ui.compose.screen.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val pageBackgroundColor = SamsungColorScheme.background

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavigationBar(navController)
            }
        },
        containerColor = pageBackgroundColor
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { getEnterTransition(initialState, targetState) },
            exitTransition = { getExitTransition(initialState, targetState) },
            popEnterTransition = { getPopEnterTransition(initialState, targetState) },
            popExitTransition = { getPopExitTransition(initialState, targetState) }
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
    }
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
