package com.example.blockchainproject.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.blockchainproject.data.entity.AccountInfo
import com.google.gson.Gson

class SharedPrefsHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_SAVED_ADDRESS = "saved_tron_address"
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

    private val KEY_ACCOUNT_INFO_MAINNET = "key_account_info_mainnet"
    private val KEY_ACCOUNT_INFO_TESTNET = "key_account_info_testnet"

    fun saveAccountInfo(info: AccountInfo, isMainNet: Boolean) {
        val key = if (isMainNet) KEY_ACCOUNT_INFO_MAINNET else KEY_ACCOUNT_INFO_TESTNET
        val json = gson.toJson(info)
        prefs.edit().putString(key, json).apply()
    }

    fun getSavedAccountInfo(isMainNet: Boolean): AccountInfo? {
        val key = if (isMainNet) KEY_ACCOUNT_INFO_MAINNET else KEY_ACCOUNT_INFO_TESTNET
        val json = prefs.getString(key, null) ?: return null
        return gson.fromJson(json, AccountInfo::class.java)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
    fun setIsMainNet(isMainNet: Boolean) {
        prefs.edit().putBoolean("is_main_net", isMainNet).apply()
    }

    fun getIsMainNet(): Boolean {
        return prefs.getBoolean("is_main_net", true)
    }

}
