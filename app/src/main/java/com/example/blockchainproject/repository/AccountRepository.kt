package com.example.blockchainproject.repository

import com.example.blockchainproject.data.entity.AccountInfo
import com.example.blockchainproject.data.entity.AccountRequest
import com.example.blockchainproject.data.entity.Transaction
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AccountRepository {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val jsonMediaType = "application/json".toMediaType()

    suspend fun getAccountDetails(address: String): Boolean {
        return withContext(Dispatchers.IO) {
            val accountRequest = AccountRequest(address = address)
            val requestBody = gson.toJson(accountRequest).toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://api.shasta.trongrid.io/wallet/getaccount")
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute()
                return@withContext response.isSuccessful && response.body?.string()?.isNotEmpty() == true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }

    suspend fun fetchAccountInfo(address: String): AccountInfo? {
        return withContext(Dispatchers.IO) {
            val accountRequest = AccountRequest(address)
            val requestBody = gson.toJson(accountRequest).toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://nile.trongrid.io/wallet/getaccount")
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
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getTransactions(address: String): List<Transaction> {
        return withContext(Dispatchers.IO) {
            val body = mapOf(
                "account" to address,
                "limit" to 20,
                "reverse" to true
            )
            val requestBody = gson.toJson(body).toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://nile.trongrid.io/wallet/gettransactionsrelated")
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { json ->
                        val jsonObject = JSONObject(json)
                        val txArray = jsonObject.optJSONArray("transaction") ?: return@withContext emptyList()
                        (0 until txArray.length()).mapNotNull { i ->
                            val tx = txArray.getJSONObject(i)
                            val rawData = tx.getJSONObject("raw_data")
                            val contract = rawData.getJSONArray("contract").getJSONObject(0)
                            val parameter = contract.getJSONObject("parameter").getJSONObject("value")

                            val toAddress = parameter.optString("to_address")
                            val ownerAddress = parameter.optString("owner_address")
                            val value = parameter.optLong("amount", 0L)
                            val type = if (toAddress == address) "incoming" else "outgoing"
                            val timestamp = rawData.optLong("timestamp", System.currentTimeMillis())

                            Transaction(
                                hash = tx.optString("txID"),
                                amount = value,
                                type = type,
                                timestamp = timestamp
                            )
                        }
                    } ?: emptyList()
                } else emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

}