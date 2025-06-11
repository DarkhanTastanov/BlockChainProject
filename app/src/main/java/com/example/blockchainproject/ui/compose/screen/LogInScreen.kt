package com.example.blockchainproject.ui.compose.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    val context = LocalContext.current

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB2EBF2)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .blur(0.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.15f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                GlassOutlinedTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        isValidInputFormat = it.isEmpty() || (it.startsWith("T") && it.length == 34)
                        if (isLoginSuccessful == false && isValidInputFormat) {
                            loginViewModel.resetLoginState()
                        }
                    },
                    label = "Tron Address",
                    isError = !isValidInputFormat,
                    isValid = isValidInputFormat && address.isNotEmpty(),
                    supportingText = if (!isValidInputFormat) "Invalid Tron address format or account not found." else null,
                    modifier = Modifier.fillMaxWidth()
                )

                GlassRedButton(
                    text = "Open TronLink Wallet",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("tronlink://wallet")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "TronLink app not found", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                GlassRedButton(
                    text = "Log In",
                    onClick = {
                        if (address.startsWith("T") && address.length == 34) {
                            loginViewModel.checkAccountAndLogin(address, save = true)
                        } else {
                            isValidInputFormat = false
                        }
                    },
                    enabled = !isLoading && isValidInputFormat && address.isNotEmpty(),
                    loading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}


@Composable
fun GlassRedButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    loading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = if (enabled) 1f else 0.5f
            }
            .clip(shape)
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
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .blur(10.dp)
                .background(Color.Red.copy(alpha = 0.15f))
                .clickable(enabled = enabled, onClick = onClick),

            )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color(0xFFFF4444),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = Color(0xFFFF4444),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun GlassOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    isValid: Boolean,
    supportingText: String?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    val focusedBorderColor = when {
        isError -> Color(0xFFFF4444)
        isValid -> Color(0xFF4CAF50)
        value.isEmpty() -> Color.White
        else -> MaterialTheme.colorScheme.primary
    }

    val unfocusedBorderColor = when {
        isError -> Color(0xFFFF8888)
        isValid -> Color(0xFF81C784)
        value.isEmpty() -> Color.White
        else -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .clip(shape)
            .blur(10.dp)
            .background(Color.White.copy(alpha = 0.15f))
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        supportingText = {
            if (isError && supportingText != null) {
                Text(supportingText)
            }
        },
        modifier = Modifier
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focusedBorderColor,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorBorderColor = Color(0xFFFF4444),
            errorContainerColor = Color.Transparent,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black,
            ),
        shape = shape
    )
}

