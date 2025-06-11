package com.example.blockchainproject.ui.compose.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.blockchainproject.ui.compose.screen.HistoryScreen
import com.example.blockchainproject.ui.compose.screen.HomeScreen
import com.example.blockchainproject.ui.compose.screen.LoginScreen
import com.example.blockchainproject.ui.compose.screen.TransactionDetailsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavigationBar(navController)
            }        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(navController)
            }

            composable("home") { HomeScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            ) }
            composable("history") { HistoryScreen(navController) }
            composable(
                "transaction_details/{hash}",
                arguments = listOf(navArgument("hash") { type = NavType.StringType }),
                enterTransition = { fadeIn() + slideInHorizontally() },
                exitTransition = { fadeOut() + slideOutHorizontally() }
            ) {
                val hash = it.arguments?.getString("hash") ?: ""
                TransactionDetailsScreen(hash)
            }

        }
    }
}
