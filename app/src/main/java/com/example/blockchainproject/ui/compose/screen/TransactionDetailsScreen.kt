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
import com.example.blockchainproject.data.local.SharedPrefsHelper
import com.example.blockchainproject.repository.AccountRepository
import com.example.blockchainproject.ui.viewmodel.TransactionDetailsViewModel
import com.example.blockchainproject.ui.viewmodel.factory.TransactionDetailsViewModelFactory

@Composable
fun TransactionDetailsScreen(hash: String, address: String) {
    val context = LocalContext.current
    val repository = remember { AccountRepository(context) }
    val sharedPrefs = remember { SharedPrefsHelper(context) }

    val viewModel: TransactionDetailsViewModel = viewModel(
        factory = TransactionDetailsViewModelFactory(repository, sharedPrefs)
    )

    val isLoading by viewModel.isLoading.collectAsState()

    val transaction by viewModel.transaction.collectAsState()

    LaunchedEffect(hash, address) {
        viewModel.loadTransactionByHash(address, hash)
    }

//    val url = "https://nile.tronscan.org/#/transaction/$hash"
    val transactionUrl by viewModel.transactionUrl.collectAsState()

    val baseGlassColor = SamsungColorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(baseGlassColor)
            .border(
                width = 2.dp,
                color = SamsungColorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {

            Text(
                text = "Transaction Details",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = SamsungColorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(color = SamsungColorScheme.primaryContainer)
            } else {
                transaction?.let {

                        DetailRow("Hash", it.hash)
                        DetailRow("Amount ", "${it.amount / 1_000_000.0} TRX")
                        DetailRow("Type" , it.type)
                        DetailRow("Timestamp ", formatTimestamp(it.timestamp))


                } ?: Text("Transaction not found.", color = SamsungColorScheme.error)
            }


            Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                context.startActivity(intent)
                transactionUrl?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamsungColorScheme.primary)
        ) {
            Text("Open in Browser", color = Color.White)
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = SamsungColorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = SamsungColorScheme.primary
        )
    }
}


private fun formatTimestamp(timestamp: Long): String {
    return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(java.util.Date(timestamp))
}
