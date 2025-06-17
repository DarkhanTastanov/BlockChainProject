package com.example.blockchainproject.ui.compose.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.blockchainproject.R

@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(LocalContext.current))
) {
    val transactions by viewModel.filteredTransactions().collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filter by viewModel.filter.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
        viewModel.loadTransactions()
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
            .padding(16.dp)
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
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    color = SamsungColorScheme.primaryContainer
                    )
            } else {
                LazyColumn {
                    items(transactions) { tx ->
                        AnimatedVisibility(visible = true) {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        val address = if (tx.type == "incoming") tx.toAddress else tx.fromAddress
                                        navController.navigate("transaction_details/${address}/${tx.hash}")
                                    }

                            ) {
                                CompositionLocalProvider(
                                LocalContentColor provides SamsungColorScheme.primary
                            ){
                                Column(
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text("Hash: ${tx.hash.take(12)}...")
                                    Text("Amount: ${tx.amount / 1_000_000.0} TRX")
                                    Text("Type: ${tx.type}")
                                    Text("Date: ${Date(tx.timestamp)}")
                                }
                            }

                            }
                        }
                    }
                }

            }
        }
    }

}
