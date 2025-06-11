package com.example.blockchainproject.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.blockchainproject.data.entity.AccountInfo
import com.example.blockchainproject.data.entity.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefsHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_SAVED_ADDRESS = "saved_tron_address"
        private const val KEY_ACCOUNT_INFO = "account_info"
    }

    fun saveAddress(address: String) {
        prefs.edit().putString(KEY_SAVED_ADDRESS, address).apply()
    }

    fun getSavedAddress(): String? {
        return prefs.getString(KEY_SAVED_ADDRESS, null)
    }

    fun clearAddress() {
        prefs.edit().remove(KEY_SAVED_ADDRESS).apply()
    }


    fun saveAccountInfo(info: AccountInfo) {
        val json = gson.toJson(info)
        prefs.edit().putString(KEY_ACCOUNT_INFO, json).apply()
    }

    fun getSavedAccountInfo(): AccountInfo? {
        val json = prefs.getString(KEY_ACCOUNT_INFO, null) ?: return null
        return try {
            gson.fromJson(json, AccountInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString("cached_transactions", json).apply()
    }

    fun getSavedTransactions(): List<Transaction> {
        val json = prefs.getString("cached_transactions", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

}
