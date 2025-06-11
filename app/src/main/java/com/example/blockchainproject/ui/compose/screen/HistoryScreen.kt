package com.example.blockchainproject.ui.compose.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.navigation.NavController
import com.example.blockchainproject.ui.viewmodel.HistoryViewModel
import com.example.blockchainproject.ui.viewmodel.factory.HistoryViewModelFactory
import java.util.Date
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(LocalContext.current))
) {
    val transactions by viewModel.filteredTransactions().collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filter by viewModel.filter.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTransactions()
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
    ){
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("all", "incoming", "outgoing").forEach { type ->
                    GlassRedButton(
                        text = type.replaceFirstChar { it.uppercase() },
                        onClick = { viewModel.setFilter(type) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }


            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn {
                    items(transactions) { tx ->
                        AnimatedVisibility(visible = true) {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        val address = if (tx.type == "incoming") tx.fromAddress else tx.toAddress
                                        navController.navigate("transaction_details/${address}/${tx.hash}")
                                    }

                            ) {
                                Column {
                                    Text("Hash: ${tx.hash.take(12)}...", color = Color.Black)
                                    Text("Amount: ${tx.amount / 1_000_000.0} TRX", color = Color.Black)
                                    Text("Type: ${tx.type}", color = Color.Black)
                                    Text("Date: ${Date(tx.timestamp)}", color = Color.Black)
                                }
                            }
                        }
                    }
                }

            }
        }
    }

}
