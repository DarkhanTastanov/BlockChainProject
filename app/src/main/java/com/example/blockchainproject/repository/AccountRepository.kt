package com.example.blockchainproject.repository

import android.content.Context
import com.example.blockchainproject.data.db.AppDatabase
import com.example.blockchainproject.data.entity.AccountInfo
import com.example.blockchainproject.data.entity.AccountRequest
import com.example.blockchainproject.data.entity.TransactionEntity
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AccountRepository(context: Context) {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    suspend fun getAccountDetails(address: String): Boolean {
        return withContext(Dispatchers.IO) {
            val accountRequest = AccountRequest(address = address)
            val requestBody = gson.toJson(accountRequest).toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://api.trongrid.io/wallet/getaccount")
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute()
                return@withContext response.isSuccessful && response.body?.string()?.isNotEmpty() == true
            }
            catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }

    suspend fun fetchAccountInfo(address: String, mainNet: Boolean): AccountInfo? {
        return withContext(Dispatchers.IO) {
            val accountRequest = AccountRequest(address)
            val requestBody = gson.toJson(accountRequest).toRequestBody(jsonMediaType)

            val url = if (mainNet) {
                "https://api.trongrid.io/wallet/getaccount"
            } else {
                "https://nile.trongrid.io/wallet/getaccount"
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { body ->
                        val json = gson.fromJson(body, Map::class.java)
                        val balance = (json["balance"] as? Double)?.toLong() ?: 0L
                        val name = (json["account_name"] as? String)
                        val transactions = (json["latest_opration_time"] != null).compareTo(false)
                        return@withContext AccountInfo(balance, name, transactions)
                    }
                }
                null
            } catch (e: CancellationException){
                null
            } catch (e: Exception) {
                null
            }
        }
    }

    private val db = AppDatabase.getInstance(context)
    private val transactionDao = db.transactionDao()
    suspend fun getTransactions(address: String, mainNet: Boolean): List<TransactionEntity> = withContext(Dispatchers.IO) {
        val url = if (mainNet) {
            "https://api.trongrid.io//v1/accounts/$address/transactions?only_confirmed=true"
        } else {
            "https://nile.trongrid.io/v1/accounts/$address/transactions?only_confirmed=true"
        }
        val request = Request.Builder().url(url).build()
        val networkType = if (mainNet) "mainnet" else "testnet" // Define networkType once

        try {
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) return@withContext transactionDao.getTransactionsByNetwork(networkType = networkType)

                val json = JSONObject(resp.body?.string().orEmpty())
                val arr = json.optJSONArray("data") ?: return@withContext transactionDao.getTransactionsByNetwork(networkType)

                val result = (0 until arr.length()).mapNotNull { i ->
                    val tx = arr.getJSONObject(i)
                    val hash = tx.optString("txID", "")
                    val timestamp = tx.optLong("block_timestamp", 0L)
                    val raw = tx.optJSONObject("raw_data") ?: return@mapNotNull null
                    val contract = raw.optJSONArray("contract")?.optJSONObject(0)
                    val param = contract?.optJSONObject("parameter")?.optJSONObject("value") ?: return@mapNotNull null
                    val value = param.optLong("amount", 0L)
                    val toAddress = param.optString("to_address", "")
                    val fromAddress = param.optString("from_address", "")
                    val isIncoming = toAddress == address
                    TransactionEntity(
                        hash = hash,
                        amount = value,
                        type = if (isIncoming) "outgoing" else "incoming",
                        timestamp = timestamp,
                        toAddress = toAddress,
                        fromAddress = fromAddress,
                        networkType = if (mainNet) "mainnet" else "testnet"
                    )
                }
                transactionDao.clearByNetwork(networkType)
                transactionDao.insertAll(result)
                return@withContext result
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.printStackTrace()

            return@withContext transactionDao.getAll()
        }
    }

    suspend fun getLocalTransactions(): List<TransactionEntity> {
        return withContext(Dispatchers.IO) {
            transactionDao.getAll()
        }
    }

    suspend fun clearTransactions() {
        return withContext(Dispatchers.IO) {
        transactionDao.clear()
    }}

    suspend fun getLocalTransactionsByNetwork(networkType: String): List<TransactionEntity> {
        return transactionDao.getTransactionsByNetwork(networkType)
    }

}