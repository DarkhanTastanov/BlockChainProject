package com.example.blockchainproject.ui.compose.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blockchainproject.repository.AccountRepository
import com.example.blockchainproject.ui.viewmodel.TransactionDetailsViewModel
import com.example.blockchainproject.ui.viewmodel.factory.TransactionDetailsViewModelFactory

@Composable
fun TransactionDetailsScreen(hash: String, address: String) {
    val repository = remember { AccountRepository() }

    val viewModel: TransactionDetailsViewModel = viewModel(
        factory = TransactionDetailsViewModelFactory(repository)
    )
    val transaction by viewModel.transaction.collectAsState()

    LaunchedEffect(hash, address) {
        viewModel.loadTransactionByHash(address, hash)
    }

    val context = LocalContext.current
    val url = "https://nile.tronscan.org/#/transaction/$hash"
    val baseGlassColor = Color(0xFFB2EBF2)
    val borderColors = listOf(Color(0xFFFF4444), Color(0xFFFF8888), Color(0xFFFF4444))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(baseGlassColor)
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(borderColors),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Transaction Details",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (transaction == null) {
            CircularProgressIndicator()
        } else {
            DetailRow("Hash", transaction!!.hash)
            DetailRow("Amount", "${transaction!!.amount} TRX")
            DetailRow("Type", transaction!!.type)
            DetailRow("Timestamp", formatTimestamp(transaction!!.timestamp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444))
        ) {
            Text("Open in Browser", color = Color.White)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium), color = Color.Black)
    }
}

private fun formatTimestamp(timestamp: Long): String {
    return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(timestamp))
}
