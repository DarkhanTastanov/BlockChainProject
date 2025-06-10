package com.example.blockchainproject.ui.compose.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            listOf("all", "incoming", "outgoing").forEach { type ->
                Button(onClick = { viewModel.setFilter(type) }) {
                    Text(type.capitalize())
                }
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(transactions) { tx ->
                    AnimatedVisibility(visible = true) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    navController.navigate("transaction_details/${tx.hash}")
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Hash: ${tx.hash.take(12)}...")
                                Text("Amount: ${tx.amount / 1_000_000.0} TRX")
                                Text("Type: ${tx.type}")
                                Text("Date: ${Date(tx.timestamp).toString()}")
                            }
                        }
                    }
                }
            }
        }
    }
}
