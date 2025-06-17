package com.example.blockchainproject.ui.compose.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blockchainproject.R
import com.example.blockchainproject.ui.viewmodel.HomeViewModel
import com.example.blockchainproject.ui.viewmodel.LoginViewModel
import com.example.blockchainproject.ui.viewmodel.factory.HomeViewModelFactory
import com.example.blockchainproject.ui.viewmodel.factory.LoginViewModelFactory

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(LocalContext.current)),
    loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(LocalContext.current)),
    onLogout: () -> Unit
) {
    val accountInfo by homeViewModel.accountInfo.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()
    val shouldLogout by loginViewModel.shouldLogout.collectAsState()

    LaunchedEffect(shouldLogout) {
        if (shouldLogout) {
            loginViewModel.acknowledgeLogoutHandled()
            onLogout()
        }
    }
//    Image(
//        painter = painterResource(R.drawable.back),
//        contentDescription = null,
//        modifier = Modifier.fillMaxSize(),
//        contentScale = ContentScale.Crop // or Fit, depending on your need
//    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = SamsungColorScheme.background
            )
            .padding(24.dp)
    ) {
        GlassRedButton(
            text = "Выйти",
            onClick = { loginViewModel.logout() },
            modifier = Modifier
                .align(Alignment.TopEnd)
        )

        if (accountInfo == null && isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center),
                color = SamsungColorScheme.primaryContainer)
        } else {
            accountInfo?.let { info ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides SamsungColorScheme.primary
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Баланс: ${info.balance / 1_000_000.0} TRX",
                                fontSize = 20.sp
                            )
                            Text(
                                "Имя аккаунта: ${info.name ?: "Не указано"}",
                                fontSize = 20.sp
                            )
                            Text(
                                "Количество транзакций: ${info.totalTransactions}",
                                fontSize = 20.sp,
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            GlassRedButton(
                                text = "Обновить",
                                onClick = { homeViewModel.reloadManually() },
                                loading = isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

            } ?: Text("Нет сохранённых данных", modifier = Modifier.align(Alignment.Center))
        }
    }
}
