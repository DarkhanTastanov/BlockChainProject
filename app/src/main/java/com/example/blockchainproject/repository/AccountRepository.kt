package com.example.blockchainproject.repository

import com.example.blockchainproject.data.entity.AccountRequest
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
}