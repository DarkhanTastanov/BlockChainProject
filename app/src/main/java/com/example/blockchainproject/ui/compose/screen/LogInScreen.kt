package com.example.blockchainproject.ui.compose.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.blockchainproject.ui.viewmodel.LoginViewModel
import com.example.blockchainproject.ui.viewmodel.factory.LoginViewModelFactory

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(LocalContext.current))
) {
    var address by remember { mutableStateOf("") }
    var isValidInputFormat by remember { mutableStateOf(true) }

    val isLoginSuccessful by loginViewModel.isLoginSuccessful.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val shouldLogout by loginViewModel.shouldLogout.collectAsState()

    LaunchedEffect(Unit) {
        val autoAddress = loginViewModel.autoLoginIfPossible()
        if (autoAddress != null) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    LaunchedEffect(isLoginSuccessful) {
        if (isLoginSuccessful == true) {
            navController.navigate("home") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            loginViewModel.resetLoginState()
        } else if (isLoginSuccessful == false) {
            isValidInputFormat = false
        }
    }

    LaunchedEffect(shouldLogout) {
        if (shouldLogout) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
            loginViewModel.acknowledgeLogoutHandled()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
                isValidInputFormat = it.isEmpty() || (it.startsWith("T") && it.length == 34)
                if (isLoginSuccessful == false && isValidInputFormat) {
                    loginViewModel.resetLoginState()
                }
            },
            label = { Text("Tron Address") },
            isError = !isValidInputFormat,
            supportingText = {
                if (!isValidInputFormat) {
                    Text("Invalid Tron address format or account not found.")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (address.startsWith("T") && address.length == 34) {
                    loginViewModel.checkAccountAndLogin(address, save = true)
                } else {
                    isValidInputFormat = false
                }
            },
            enabled = !isLoading && isValidInputFormat && address.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Text("Log In")
            }
        }
    }
}
