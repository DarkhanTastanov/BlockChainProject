package com.example.blockchainproject.ui.compose.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blockchainproject.ui.viewmodel.HomeViewModel
import com.example.blockchainproject.ui.viewmodel.factory.HomeViewModelFactory

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(LocalContext.current))
) {
    val accountInfo by homeViewModel.accountInfo.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadAccountData()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            accountInfo?.let { info ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Баланс: ${info.balance / 1_000_000.0} TRX")
                    Text("Имя аккаунта: ${info.name ?: "Не указано"}")
                    Text("Количество транзакций: ${info.totalTransactions}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { homeViewModel.loadAccountData() }) {
                        Text("Обновить")
                    }
                }
            } ?: Text("Ошибка загрузки данных")
        }
    }
}
